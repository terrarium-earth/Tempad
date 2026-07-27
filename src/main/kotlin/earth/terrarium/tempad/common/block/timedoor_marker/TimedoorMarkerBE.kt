package earth.terrarium.tempad.common.block.timedoor_marker

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.common.network.s2c.OpenTimedoorMarker
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.canAccess
import earth.terrarium.tempad.common.utils.sendToClient
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState

class TimedoorMarkerBE(pos: BlockPos, state: BlockState) : AbstractMarkerBe(ModBlocks.doorpointIronBE, pos, state) {
    override fun openScreen(player: Player) {
        OpenTimedoorMarker(blockPos, color, posName.string, canAccess, locked).sendToClient(player)
    }

    override fun canAccess(player: GameProfile): Boolean {
        return owner?.let { canAccess || player.id == it.id } == true
    }
}