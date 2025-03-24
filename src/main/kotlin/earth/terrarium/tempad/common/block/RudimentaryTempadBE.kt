package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.upgrades
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB

class RudimentaryTempadBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.rudimentaryTempadBE, pos, state) {
    var timedoorId: Int? = null

    fun openTimedoor() {
        val level = level
        if(level == null || level.isClientSide()) return
        val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
        if(timedoorId?.let { id -> level.getEntity(id) } != null) return nearby.error(Component.translatable("tempad.error.timedoor_already_open"))
        safeLet(portalTarget, upgrades, chronons, owner) { pos, upgrades, chronons, player ->
            pos.get(upgrades, chronons)?.let {
                TimedoorEntity.openTimedoor(player, this, it) {
                    timedoorId = it.id
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