package earth.terrarium.tempad.common.block.timedoor_marker

import earth.terrarium.tempad.common.network.s2c.OpenSpatialAnchor
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.accessId
import earth.terrarium.tempad.common.registries.color
import earth.terrarium.tempad.common.utils.sendToClient
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class TimedoorMarkerBE(pos: BlockPos, state: BlockState) : AbstractMarkerBe(ModBlocks.timedoorMarkerBE, pos, state) {
    override fun openScreen(player: Player) {
        OpenSpatialAnchor(blockPos, color, posName.string, accessId).sendToClient(player)
    }
}