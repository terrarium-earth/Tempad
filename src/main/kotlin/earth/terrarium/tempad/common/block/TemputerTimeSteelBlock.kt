package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.block.WorkstationBlock.Companion.HAS_TAPE
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItemAccess
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class TemputerTimeSteelBlock(props: Properties) : BaseEntityBlock(props), MenuProvider {
    companion object {
        val SOUTH_SHAPE: VoxelShape = Shapes.or(
            box(1.0, 0.0, 0.0, 15.0, 2.0, 16.0),
            box(1.0, 2.0, 0.0, 15.0, 11.0, 10.0),
        )

        val NORTH_SHAPE: VoxelShape = Shapes.or(
            box(1.0, 0.0, 0.0, 15.0, 2.0, 16.0),
            box(1.0, 2.0, 6.0, 15.0, 11.0, 16.0),
        )

        val EAST_SHAPE: VoxelShape = Shapes.or(
            box(0.0, 0.0, 1.0, 16.0, 2.0, 15.0),
            box(0.0, 2.0, 1.0, 10.0, 11.0, 15.0),
        )

        val WEST_SHAPE: VoxelShape = Shapes.or(
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
        val storageBE = level.getBlockEntity(pos) as? TemputerTimeSteelBE ?: return InteractionResult.PASS

        if (storageBE.owner == null) {
            storageBE.owner = player.gameProfile
        } else if (storageBE.locked && storageBE.owner?.id != player.gameProfile.id) {
            player.sendOverlayMessage(Component.translatable("error.tempad.block_locked", name).withColor(Tempad.ORANGE.value))
            return InteractionResult.FAIL
        }

        /*
        (TempadAppRegistry[stack.defaultApp, player, ctx]
            ?: TempadAppRegistry[ModApps.portalSetup, player, ctx])!!.openMenu(player as ServerPlayer)
         */

        player.openMenu(this)

        return InteractionResult.SUCCESS
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        return createTickerHelper(type, ModBlocks.temputerTimeSteelBE) { level, pos, state, blockEntity ->
            blockEntity.tick()
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING).add(BlockStateProperties.TRIGGERED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
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

    fun relativeDir(state: BlockState): Direction {
        return state.getValue(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        orientation: Orientation?,
        isMoving: Boolean
    ) {
        val neighborPowered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above())
        val blockEntity = level.getBlockEntity(pos) as? TemputerTimeSteelBE ?: return
        blockEntity.active = neighborPowered
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = simpleCodec(::TemputerTimeSteelBlock)

    override fun newBlockEntity(var1: BlockPos, var2: BlockState): BlockEntity = TemputerTimeSteelBE(var1, var2)

    override fun getDisplayName(): Component {
        return Component.translatable("block.tempad.temputer_time_steel")
    }

    override fun createMenu(
        var1: Int,
        var2: Inventory,
        var3: Player,
    ): AbstractContainerMenu {
        return ModMenus.TemputerMenu(var1, var2)
    }
}