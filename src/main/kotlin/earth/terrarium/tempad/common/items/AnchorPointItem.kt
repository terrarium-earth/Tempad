package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.common.registries.*
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class AnchorPointItem: BlockItem(ModBlocks.anchorPoint, Properties()) {
    override fun updateCustomBlockEntityTag(
        pos: BlockPos,
        level: Level,
        player: Player?,
        stack: ItemStack,
        state: BlockState,
    ): Boolean {
        level.getBlockEntity(pos)?.let { blockEntity ->
            stack.color?.let { blockEntity.color = it }
            player?.let { blockEntity.owner = it.gameProfile }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state)
    }
}