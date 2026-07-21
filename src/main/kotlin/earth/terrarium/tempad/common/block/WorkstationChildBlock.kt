package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.access.WorkstationItemAccess
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItemAccess
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class WorkstationChildBlock(props: Properties) : Block(props) {
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
        val ctx = ItemAccessAddress(ModItemAccess.block, storagePos)

        val stack = storageBE.inventory.stack(0)
        if (stack.isEmpty) return InteractionResult.PASS

        if (storageBE.owner == null) {
            storageBE.owner = player.gameProfile
        } else if (stack.locked && storageBE.owner?.id != player.gameProfile.id) {
            player.sendOverlayMessage(Component.translatable("error.tempad.block_locked", name).withColor(Tempad.ORANGE.value))
            return InteractionResult.FAIL
        }

        (AppRegistry[stack.defaultApp, player, ctx, true]
            ?: AppRegistry[ModApps.portalSetup, player, ctx, true])!!.openMenu(player as ServerPlayer)
        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING).add(BlockStateProperties.TRIGGERED)
    }

    override fun updateShape(
        state: BlockState,
        level: LevelReader,
        ticks: ScheduledTickAccess,
        neighborPos: BlockPos,
        facing: Direction,
        neighbourPos: BlockPos,
        facingState: BlockState,
        random: RandomSource
    ): BlockState {
        return if (facing == relativeDir(state) && facingState.block == Blocks.AIR) {
            Blocks.AIR.defaultBlockState()
        } else {
            super.updateShape(state, level, ticks, neighborPos, facing, neighbourPos, facingState, random)
        }
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

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
        orientation: Orientation?,
        isMoving: Boolean
    ) {
        val ogBlock = getPos(state, pos)
        val neighborPowered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above()) || level.hasNeighborSignal(ogBlock)
        val controllerPos = getPos(state, pos)
        val blockEntity = level.getBlockEntity(controllerPos) as? WorkstationBE ?: return
        if (neighborPowered && !blockEntity.active) {
            blockEntity.activateLeft()
        } else if (!neighborPowered && blockEntity.active) {
            blockEntity.deactivateLeft()
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
        level: LevelReader,
        pos: BlockPos,
        state: BlockState,
        includeData: Boolean,
        player: Player
    ): ItemStack {
        return ItemStack(ModBlocks.workstation)
    }
}

private inline val Number.px: Double get() = this.toDouble() / 16.0
