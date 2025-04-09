package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.context.WorkstationContext
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.get
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class WorkstationChildBlock : Block(Properties.of().strength(3.0f, 1200f)) {
    companion object {
        val NORTH_SHAPE: VoxelShape = Shapes.or(
            box(1.0, 0.0, 0.0, 15.0, 2.0, 16.0),
            box(1.0, 2.0, 0.0, 15.0, 11.0, 10.0),
        )

        val SOUTH_SHAPE: VoxelShape = Shapes.or(
            box(1.0, 0.0, 0.0, 15.0, 2.0, 16.0),
            box(1.0, 2.0, 6.0, 15.0, 11.0, 16.0),
        )

        val WEST_SHAPE: VoxelShape = Shapes.or(
            box(0.0, 0.0, 1.0, 16.0, 2.0, 15.0),
            box(0.0, 2.0, 1.0, 10.0, 11.0, 15.0),
        )

        val EAST_SHAPE: VoxelShape = Shapes.or(
            box(0.0, 0.0, 1.0, 16.0, 2.0, 15.0),
            box(6.0, 2.0, 1.0, 16.0, 11.0, 15.0),
        )
    }

    init {
        this.registerDefaultState(
            stateDefinition
                .any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.TRIGGERED, false)
        )
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult,
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val storagePos = getPos(state, pos)
        val storageBE = level.getBlockEntity(storagePos) as? WorkstationBE ?: return InteractionResult.PASS
        val ctx = WorkstationContext(player, storagePos)

        val stack = storageBE.inventory[0]
        if (stack.isEmpty || (stack.locked && player.gameProfile.id != stack.owner?.id)) return InteractionResult.PASS

        (AppRegistry[stack.defaultApp, ctx, true]
            ?: AppRegistry[ModApps.portalSetup, ctx, true])!!.openMenu(player as ServerPlayer)
        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING).add(BlockStateProperties.TRIGGERED)
    }

    override fun updateShape(
        state: BlockState,
        facing: Direction,
        facingState: BlockState,
        level: LevelAccessor,
        currentPos: BlockPos,
        neighborPos: BlockPos,
    ): BlockState {
        return if (facing == relativeDir(state) && facingState.block == Blocks.AIR) {
            Blocks.AIR.defaultBlockState()
        } else {
            super.updateShape(state, facing, facingState, level, currentPos, neighborPos)
        }
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.INVISIBLE

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape {
        return when (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction.NORTH -> NORTH_SHAPE
            Direction.SOUTH -> SOUTH_SHAPE
            Direction.WEST -> WEST_SHAPE
            Direction.EAST -> EAST_SHAPE
            else -> NORTH_SHAPE
        }
    }

    fun getPos(state: BlockState, pos: BlockPos): BlockPos {
        return pos.relative(relativeDir(state))
    }

    fun relativeDir(state: BlockState): Direction {
        return Direction.fromYRot(state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90.0)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean,
    ) {
        val ogBlock = getPos(state, pos)
        val neighborPowered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above()) || level.hasNeighborSignal(ogBlock)
        val currentlyPowered = state.getValue(BlockStateProperties.TRIGGERED)
        if (neighborPowered && !currentlyPowered) {
            level.scheduleTick(pos, this, 10)
            level.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, true), 2)
        } else if (!neighborPowered && currentlyPowered) {
            level.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, false), 2)
        }
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        super.tick(state, level, pos, random)
        val controllerPos = getPos(state, pos)
        val blockEntity = level.getBlockEntity(controllerPos) as? WorkstationBE ?: return
        if (state.getValue(BlockStateProperties.TRIGGERED)) {
            blockEntity.openTimedoor()
            level.scheduleTick(pos, this, 20)
        }
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!level.isClientSide) {
            val blockpos = getPos(state, pos)
            val blockstate = level.getBlockState(blockpos)
            val dir = blockstate.getValue(BlockStateProperties.HORIZONTAL_FACING)
            if (blockstate.`is`(ModBlocks.workstation) && state.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir) {
                level.destroyBlock(blockpos, !player.isCreative)
                level.levelEvent(player, 2001, blockpos, getId(blockstate))
            }
        }
        return super.playerWillDestroy(level, pos, state, player)
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player,
    ): ItemStack {
        return ItemStack(ModBlocks.workstation)
    }
}

private inline val Number.px: Double get() = this.toDouble() / 16.0
