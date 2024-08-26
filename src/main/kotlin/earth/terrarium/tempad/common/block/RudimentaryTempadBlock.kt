package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.targetPos
import earth.terrarium.tempad.common.registries.targetLocation
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class RudimentaryTempadBlock : BaseEntityBlock(Properties.of().noOcclusion()) {
    companion object {
        val codec: MapCodec<out BaseEntityBlock> = simpleCodec { ModBlocks.rudimentaryTempad }
        val hasCardProperty = BooleanProperty.create("has_card")

        val shape = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
    }

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.TRIGGERED, false)
                .setValue(hasCardProperty, false)
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val hasCard = context.itemInHand.targetPos != null
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection).setValue(hasCardProperty, hasCard)
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        return mutableListOf(
            ModItems.rudimentaryTempad.stack {
                (params.getParameter(LootContextParams.BLOCK_ENTITY) as? RudimentaryTempadBE)?.let {
                    targetPos = it.targetLocation
                }
            }
        )
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        result: BlockHitResult,
    ): ItemInteractionResult {
        return super.useItemOn(stack, state, level, pos, player, hand, result)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean,
    ) {
        val flag = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above())
        val flag1 = state.getValue(BlockStateProperties.TRIGGERED)
        if (flag && !flag1) {
            level.scheduleTick(pos, this, 4)
            level.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, true), 2)
        } else if (!flag && flag1) {
            level.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, false), 2)
        }
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        super.tick(state, level, pos, random)
        val blockEntity = level.getBlockEntity(pos) as? RudimentaryTempadBE ?: return
        blockEntity.openTimedoor()
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = codec
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = RudimentaryTempadBE(pos, state)
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.TRIGGERED, hasCardProperty)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return shape
    }
}