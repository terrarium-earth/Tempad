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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

class RudimentaryTempadBlock : BaseEntityBlock(Properties.of()) {
    companion object {
        val codec: MapCodec<out BaseEntityBlock> = simpleCodec { ModBlocks.rudimentaryTempad }
    }

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.TRIGGERED, false)
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(BlockStateProperties.FACING, context.horizontalDirection.opposite)
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
        builder.add(BlockStateProperties.FACING, BlockStateProperties.TRIGGERED)
    }
}