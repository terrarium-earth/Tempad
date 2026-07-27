package earth.terrarium.tempad.common.block

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.utils.GAME_PROFILE_CODEC
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class RudimentaryTempadBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.temputerIronBE, pos, state) {
    var timedoorId: UUID? = null
    var portalTarget: LocationGetter? = null
    var chronons: SimpleEnergyHandler = SimpleEnergyHandler(CommonConfigCache.RudimentaryTempad.capacity)
    var owner: GameProfile? = null

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putInt("Chronons", chronons.amountAsInt)
        owner?.let { output.store("Owner", GAME_PROFILE_CODEC.codec(), it) }
        portalTarget?.let { output.store("PortalTarget", LocationGetter.codec, it) }
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        chronons.set(input.getIntOr("Chronons", 0))
        owner = input.read("Owner", GAME_PROFILE_CODEC.codec()).getOrNull()
        portalTarget = input.read("PortalTarget", LocationGetter.codec).getOrNull()
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveCustomOnly(registries)
    }

    override fun collectImplicitComponents(components: DataComponentMap.Builder) {
        super.collectImplicitComponents(components)
        components[ModComponents.portalTarget] = portalTarget
        components[ModComponents.chrononContent] = chronons.amountAsInt
    }

    override fun applyImplicitComponents(components: DataComponentGetter) {
        super.applyImplicitComponents(components)
        portalTarget = components.get(ModComponents.portalTarget)
        chronons.set(components.getOrDefault(ModComponents.chrononContent, 0))
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    fun openTimedoor() {
        val level = level
        if(level == null || level !is ServerLevel) return
        val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
        if(timedoorId?.let { id -> level.entities.get(id) } != null) return nearby.error(Component.translatable("tempad.error.timedoor_already_open").withColor(Tempad.ORANGE.value))
        safeLet(portalTarget, chronons, owner) { pos, chronons, player ->
            pos.get(null, chronons)?.let {
                val (provider, id) = (pos as? IndirectLocation).let { it?.provider to it?.id }
                val error = TimedoorEntity.openTimedoor(player, this, provider, id, it, pos.calculateCost(level, blockPos, null, chronons)) {
                    this.timedoorId = it.uuid
                    it.yRot += 180
                    it.instability = 15
                }
                error?.let { msg ->
                    nearby.error(msg)
                }
            }
        }
    }

    fun List<ServerPlayer>.error(msg: Component) {
        forEach { it.sendOverlayMessage(msg) }
    }
}