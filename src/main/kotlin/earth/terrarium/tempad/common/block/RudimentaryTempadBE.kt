package earth.terrarium.tempad.common.block

import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.items.AttachmentChrononContainer
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.targetLocation
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
/*
data class TempadBlockData(): MenuContent<TempadBlockData> {
    override fun serializer(): MenuContentSerializer<TempadBlockData> {
        TODO("Not yet implemented")
    }
}*/

class RudimentaryTempadBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.rudimentaryTempadBE, pos, state) {
    val chronons = AttachmentChrononContainer(this, 4000)
    var timedoorId: Int? = null

    fun openTimedoor() {
        val level = level
        if(level == null || level.isClientSide()) return
        val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
        if(timedoorId?.let { id -> level.getEntity(id) } != null) return nearby.error(Component.translatable("tempad.error.timedoor_already_open"))
        safeLet(targetLocation, owner) { pos, player ->
            TimedoorEntity.openTimedoor(player, this, pos) {
                timedoorId = it.id
                it.yRot += 180
                it.glitching = true
            }?.let { msg ->
                nearby.error(msg)
            }
        }
    }

    fun List<ServerPlayer>.error(msg: Component) {
        forEach { it.displayClientMessage(msg, true) }
    }
}