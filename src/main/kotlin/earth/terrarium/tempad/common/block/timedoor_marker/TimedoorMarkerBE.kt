package earth.terrarium.tempad.common.block.timedoor_marker

import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.network.s2c.OpenTimedoorMarker
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.accessId
import earth.terrarium.tempad.common.registries.color
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.utils.sendToClient
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState

class TimedoorMarkerBE(pos: BlockPos, state: BlockState) : AbstractMarkerBe(ModBlocks.timedoorMarkerBE, pos, state) {
    override fun openScreen(player: Player) {
        OpenTimedoorMarker(blockPos, color, posName.string, PlayerAccessApi.ids, accessId, locked).sendToClient(player)
    }
}