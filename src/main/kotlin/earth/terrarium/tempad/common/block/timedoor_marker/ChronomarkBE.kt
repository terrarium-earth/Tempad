package earth.terrarium.tempad.common.block.timedoor_marker

import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class ChronomarkBE(pos: BlockPos, state: BlockState) : AbstractMarkerBe(ModBlocks.chronomarkBE, pos, state) {
    override fun openScreen(player: Player) {
        TODO("Not yet implemented")
    }
}