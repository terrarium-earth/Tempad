package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBe
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.facingAngle
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class DoorpointItem(block: Block, props: Properties): BlockItem(block, props) {
    override fun updateCustomBlockEntityTag(
        pos: BlockPos,
        level: Level,
        player: Player?,
        stack: ItemStack,
        state: BlockState,
    ): Boolean {
        (level.getBlockEntity(pos) as? AbstractMarkerBe)?.let { blockEntity ->
            stack.color?.let { blockEntity.color = it }
            stack.get(DataComponents.CUSTOM_NAME)?.let { blockEntity.posName = it }
            stack.anchorId?.let { blockEntity.id = it }
            blockEntity.angle = blockEntity.facingAngle.toInt()
            blockEntity.yOffset = if(state.getValue(BlockStateProperties.UP)) 0.5f else -2f
            player?.let { blockEntity.owner = it.gameProfile }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state)
    }

    override fun place(context: BlockPlaceContext): InteractionResult {
        context.itemInHand.owner?.let { owner ->
            if (owner.id != context.player?.gameProfile?.id) {
                context.player?.sendSystemMessage(Component.translatable("block.tempad.marker.owner_mismatch.place").withColor(Tempad.ORANGE.value))
                return InteractionResult.FAIL
            }
        }
        return super.place(context)
    }
}