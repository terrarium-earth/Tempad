package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.load
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.save
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
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

class RudimentaryTempadBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.timedoorProjectorBE, pos, state) {
    var timedoorId: UUID? = null
    var portalTarget: LocationGetter? = null
    var chronons: SimpleEnergyHandler = SimpleEnergyHandler(CommonConfigCache.RudimentaryTempad.capacity)

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putInt("Chronons", chronons.amountAsInt)
        portalTarget?.let { output.store("PortalTarget", LocationGetter.codec, it) } ?: run { output.discard("PortalTarget") }
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        chronons.set(input.getIntOr("Chronons", 0))
        portalTarget = input.read("PortalTarget", LocationGetter.codec).getOrNull()
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply {
            putInt("Chronons", chronons.capacityAsInt)
            portalTarget?.let { put("PortalTarget", LocationGetter.codec.encode(it, NbtOps.INSTANCE, CompoundTag()).result().orElse(CompoundTag())) }
        }
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    fun openTimedoor() {
        val level = level
        if(level == null || level !is ServerLevel) return
        val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
        if(timedoorId?.let { id -> level.entities.get(id) } != null) return nearby.error(Component.translatable("tempad.error.timedoor_already_open").withColor(Tempad.ORANGE.value))
        safeLet(portalTarget, upgrades, chronons, owner) { pos, upgrades, chronons, player ->
            pos.get(upgrades, chronons)?.let {
                val (provider, id) = (pos as? IndirectLocation).let { it?.provider to it?.id }
                TimedoorEntity.openTimedoor(player, this, provider, id, it) {
                    this.timedoorId = it.uuid
                    it.yRot += 180
                    it.glitching = true
                }?.let { msg ->
                    nearby.error(msg)
                }
            }
        }
    }

    fun List<ServerPlayer>.error(msg: Component) {
        forEach { it.sendSystemMessage(msg, true) }
    }
}