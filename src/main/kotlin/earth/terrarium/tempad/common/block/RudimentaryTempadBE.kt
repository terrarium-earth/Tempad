package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.items.AttachmentChrononContainer
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.targetLocation
import earth.terrarium.tempad.common.registries.tempadOwner
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.common.util.FakePlayerFactory
import java.util.UUID

class RudimentaryTempadBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.rudimentaryTempadBE, pos, state) {
    val chronons = AttachmentChrononContainer(this, 4000)
    val placementPlayer: Player? get() = safeLet(level as? ServerLevel, tempadOwner, FakePlayerFactory::get)
    var timedoorId: Int? = null

    fun openTimedoor() {
        val level = level
        if(level == null || level is ClientLevel) return
        if(timedoorId?.let { id -> level.getEntity(id) } != null) return
        safeLet(targetLocation, placementPlayer) { pos, player ->
            TimedoorEntity.openTimedoor(player, this, pos) {
                timedoorId = it.id
            }?.let { msg ->
                val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
                for (sPlayer in nearby) {
                    sPlayer.displayClientMessage(msg, true)
                }
            }
        }
    }
}