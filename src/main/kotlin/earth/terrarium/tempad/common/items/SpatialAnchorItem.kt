package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.SpatialAnchorBE
import earth.terrarium.tempad.common.registries.*
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class SpatialAnchorItem: BlockItem(ModBlocks.spatialAnchor, Properties()) {
    override fun updateCustomBlockEntityTag(
        pos: BlockPos,
        level: Level,
        player: Player?,
        stack: ItemStack,
        state: BlockState,
    ): Boolean {
        (level.getBlockEntity(pos) as? SpatialAnchorBE)?.let { blockEntity ->
            stack.color?.let { blockEntity.color = it }
            stack.get(DataComponents.CUSTOM_NAME)?.let { blockEntity.posName = it }
            stack.anchorId?.let { blockEntity.id = it }
            player?.let { blockEntity.owner = it.gameProfile }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state)
    }

    override fun place(context: BlockPlaceContext): InteractionResult {
        context.itemInHand.owner?.let { owner ->
            if (owner.id != context.player?.gameProfile?.id) {
                context.player?.displayClientMessage(Component.translatable("block.tempad.spatial.anchor.owner_mismatch.place").withColor(Tempad.ORANGE.value), true)
                return InteractionResult.FAIL
            }
        }
        return super.place(context)
    }
}