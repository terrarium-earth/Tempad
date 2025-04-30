package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.upgrades
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.load
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.save
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import java.util.UUID

class RudimentaryTempadBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.timedoorProjectorBE, pos, state) {
    var timedoorId: UUID? = null
    var chrononContent: Int = 0
    var portalTarget: LocationGetter? = null

    override fun saveAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.saveAdditional(tag, registries)
        tag.putInt("Chronons", chrononContent)
        portalTarget?.let { tag.save(LocationGetter.codec, "PortalTarget", it) } ?: run { tag.remove("PortalTarget") }
    }

    override fun loadAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.loadAdditional(tag, registries)
        chrononContent = tag.getInt("Chronons")
        portalTarget = tag.load(LocationGetter.codec, "PortalTarget")
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply { saveAdditional(this, registries) }
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
        forEach { it.displayClientMessage(msg, true) }
    }
}