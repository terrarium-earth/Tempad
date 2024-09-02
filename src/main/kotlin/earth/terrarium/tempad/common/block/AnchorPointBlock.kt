package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import com.teamresourceful.resourcefullib.common.color.ConstantColors
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.AnchorPointPos
import earth.terrarium.tempad.common.location_handlers.anchorPointData
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.contains
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.GlobalPos
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
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
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.common.Tags

class AnchorPointBlock : BaseEntityBlock(Properties.of()) {
    companion object {
        val codec: MapCodec<out BaseEntityBlock> = simpleCodec { ModBlocks.anchorPoint }
    }

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH)
        )
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult,
    ): ItemInteractionResult {
        if (stack.item === ModItems.locationCard) {
            if (level.isClientSide) return ItemInteractionResult.sidedSuccess(level.isClientSide)
            val blockEntity = level.getBlockEntity(pos) as SpatialAnchorBE
            level.anchorPointData[blockEntity.posId!!]?.let {
                stack.targetPos = it
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        }

        val color = when {
            stack in Tags.Items.DYES_RED -> ConstantColors.red
            stack in Tags.Items.DYES_GREEN -> ConstantColors.green
            stack in Tags.Items.DYES_BLUE -> ConstantColors.blue
            stack in Tags.Items.DYES_YELLOW -> ConstantColors.yellow
            stack in Tags.Items.DYES_BLACK -> ConstantColors.black
            stack in Tags.Items.DYES_BROWN -> ConstantColors.brown
            stack in Tags.Items.DYES_CYAN -> ConstantColors.cyan
            stack in Tags.Items.DYES_GRAY -> ConstantColors.gray
            stack in Tags.Items.DYES_LIGHT_BLUE -> ConstantColors.powderblue
            stack in Tags.Items.DYES_LIGHT_GRAY -> ConstantColors.lightgray
            stack in Tags.Items.DYES_LIME -> ConstantColors.lime
            stack in Tags.Items.DYES_MAGENTA -> ConstantColors.magenta
            stack in Tags.Items.DYES_ORANGE -> Tempad.ORANGE
            stack in Tags.Items.DYES_PINK -> ConstantColors.pink
            stack in Tags.Items.DYES_PURPLE -> ConstantColors.purple
            stack in Tags.Items.DYES_WHITE -> ConstantColors.white
            else -> return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
        }
        (level as? ServerLevel)?.getBlockEntity(pos)?.let {
            it as SpatialAnchorBE
            it.color = color
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult,
    ): InteractionResult {
        return super.useWithoutItem(state, level, pos, player, hitResult)
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        return mutableListOf(
            ModItems.anchorPoint.stack {
                (params.getParameter(LootContextParams.BLOCK_ENTITY) as? SpatialAnchorBE)?.let {
                    color = it.color
                    set(DataComponents.CUSTOM_NAME, it.name)
                }
            }
        )
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, moved: Boolean) {
        super.onPlace(state, level, pos, oldState, moved)
        level.anchorPointData += AnchorPointPos(GlobalPos(level.dimension(), pos))
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, moved: Boolean) {
        level.getBlockEntity(pos)?.posId?.let { level.anchorPointData -= it }
        super.onRemove(state, level, pos, newState, moved)
    }

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return codec
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SpatialAnchorBE(pos, state)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BlockStateProperties.FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(BlockStateProperties.FACING, context.horizontalDirection.opposite)
    }
}