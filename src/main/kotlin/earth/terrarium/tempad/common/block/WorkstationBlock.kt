package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.common.recipe.UpgradeRecipeInput
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModRecipes
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.set
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import kotlin.jvm.optionals.getOrNull

class WorkstationBlock : BaseEntityBlock(Properties.of().noOcclusion()) {
    val codec: MapCodec<out BaseEntityBlock?> = simpleCodec { ModBlocks.workstation }
    companion object: BlockEntityTicker<WorkstationBE> {
        val HAS_TAPE: BooleanProperty = BooleanProperty.create("has_tape")

        override fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: WorkstationBE) {
            if (level !is ServerLevel) return
            if (blockEntity.cookingTime > 0) {
                blockEntity.cookingTime--
                if (blockEntity.cookingTime == 0) {
                    popResource(level, pos, Items.DRIED_KELP.stack(level.random.nextInt(3)))
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x + 0.5, pos.y + 0.25, pos.z + 0.5, 10, 0.2, 0.2, 0.2, 0.0)
                    level.setBlock(pos, state.setValue(HAS_TAPE, false), UPDATE_ALL)
                } else {
                    level.sendParticles(ParticleTypes.SMOKE, pos.x + 0.5, pos.y + 0.25, pos.z + 0.5, 2, 0.2, 0.2, 0.2, 0.0)
                }
            }
        }
    }

    init {
        this.registerDefaultState(
            stateDefinition
                .any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(HAS_TAPE, false)
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
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS
        }

        val blockEntity = level.getBlockEntity(pos) as? WorkstationBE
            ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
        if (blockEntity.inventory[0].isEmpty && stack.chronons != null) {
            blockEntity.inventory[0] = stack
            blockEntity.setChanged()
            player.setItemInHand(hand, ItemStack.EMPTY)
            level.sendBlockUpdated(pos, state, state, UPDATE_ALL)
            return ItemInteractionResult.SUCCESS
        } else if (!blockEntity.inventory[0].isEmpty && blockEntity.cookingTime == 0) {
            val recipe = level.recipeManager.getRecipeFor(ModRecipes.upgradeRecipe, UpgradeRecipeInput(blockEntity.inventory[0], stack), level).getOrNull()?.value
            if (recipe == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
            blockEntity.cookingTime = recipe.downloadTime
            blockEntity.recipe = recipe.output
            stack.shrink(1)
            blockEntity.setChanged()
            level.setBlock(pos, state.setValue(HAS_TAPE, true), UPDATE_ALL)
            if (stack.isEmpty) player.setItemInHand(hand, ItemStack.EMPTY)
            return ItemInteractionResult.SUCCESS
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T?>,
    ): BlockEntityTicker<T?>? {
        return if (level.isClientSide) null else createTickerHelper(type, ModBlocks.workstationBE, WorkstationBlock)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult,
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        val blockEntity = level.getBlockEntity(pos) as? WorkstationBE
            ?: return InteractionResult.PASS
        player.inventory.placeItemBackInInventory(blockEntity.inventory[0])
        blockEntity.inventory[0] = ItemStack.EMPTY
        level.sendBlockUpdated(pos, state, state, UPDATE_ALL)
        blockEntity.setChanged()
        return InteractionResult.SUCCESS
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING).add(HAS_TAPE)
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> = codec

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = WorkstationBE(pos, state)

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
}