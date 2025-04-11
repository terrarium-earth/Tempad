package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.api.tva_device.upgrades
import earth.terrarium.tempad.common.recipe.UpgradeRecipeInput
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.ModRecipes
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.set
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.jvm.optionals.getOrNull

class WorkstationBlock : BaseEntityBlock(Properties.of().noOcclusion().strength(3.0f, 1200f)) {
    val codec: MapCodec<out BaseEntityBlock?> = simpleCodec { ModBlocks.workstation }
    companion object: BlockEntityTicker<WorkstationBE> {
        val HAS_TAPE: BooleanProperty = BooleanProperty.create("has_tape")

        val NORTH_SHAPE: VoxelShape = Shapes.or(
            box(0.0, 0.0, 1.0, 14.0, 2.0, 15.0),
            box(0.0, 2.0, 1.0, 14.0, 4.0, 5.0),
        )

        val SOUTH_SHAPE: VoxelShape = Shapes.or(
            box(2.0, 0.0, 1.0, 16.0, 2.0, 15.0),
            box(2.0, 2.0, 11.0, 16.0, 4.0, 15.0),
        )

        val WEST_SHAPE: VoxelShape = Shapes.or(
            box(1.0, 0.0, 2.0, 15.0, 2.0, 16.0),
            box(1.0, 2.0, 2.0, 5.0, 4.0, 16.0),
        )

        val EAST_SHAPE: VoxelShape = Shapes.or(
            box(1.0, 0.0, 0.0, 15.0, 2.0, 14.0),
            box(11.0, 2.0, 0.0, 15.0, 4.0, 14.0),
        )

        override fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: WorkstationBE) {
            if (level !is ServerLevel) return
            if (blockEntity.downloadTime > 0) {
                blockEntity.downloadTime--
                if (blockEntity.downloadTime == 0) {
                    blockEntity.maxDownloadTime = 0
                    popResource(level, pos, Items.DRIED_KELP.stack(level.random.nextInt(3)))
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x + 0.5, pos.y + 0.25, pos.z + 0.5, 10, 0.2, 0.2, 0.2, 0.0)
                    safeLet(blockEntity.inventory[0].upgrades, blockEntity.recipe) { upgrades, recipe ->
                        upgrades.install(recipe)
                        blockEntity.setChanged()
                    }
                    level.setBlock(pos, state.setValue(HAS_TAPE, false), UPDATE_ALL)
                } else {
                    blockEntity.setChanged()
                    level.sendBlockUpdated(pos, state, state, UPDATE_ALL)
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
        if (blockEntity.inventory[0].isEmpty && stack.`is`(ModItems.tempad)) {
            stack.owner = player.gameProfile
            blockEntity.inventory[0] = stack
            blockEntity.setChanged()
            player.setItemInHand(hand, ItemStack.EMPTY)
            level.sendBlockUpdated(pos, state, state, UPDATE_ALL)
            return ItemInteractionResult.SUCCESS
        } else if (!blockEntity.inventory[0].isEmpty && blockEntity.downloadTime == 0) {
            val recipe = level.recipeManager.getRecipeFor(ModRecipes.upgradeRecipe, UpgradeRecipeInput(blockEntity.inventory[0], stack), level).getOrNull()?.value
            if (recipe == null || recipe.output in blockEntity.upgrades!!) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
            blockEntity.downloadTime = recipe.downloadTime
            blockEntity.maxDownloadTime = recipe.downloadTime
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

        if (blockEntity.maxDownloadTime > 0) return InteractionResult.PASS
        val stack = blockEntity.inventory[0]
        stack.owner = null
        player.inventory.placeItemBackInInventory(stack)
        blockEntity.inventory[0] = ItemStack.EMPTY
        level.sendBlockUpdated(pos, state, state, UPDATE_ALL)
        blockEntity.setChanged()
        return InteractionResult.SUCCESS
    }

    fun getPos(dir: Direction, pos: BlockPos): BlockPos {
        return pos.relative(Direction.fromYRot(dir.toYRot() - 90.0))
    }

    fun relativeDir(state: BlockState): Direction {
        return Direction.fromYRot(state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() - 90.0)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val direction: Direction = context.horizontalDirection
        val childPos: BlockPos = getPos(direction, context.clickedPos);
        val level: Level = context.level
        return if (level.getBlockState(childPos).canBeReplaced(context) && level.worldBorder.isWithinBounds(childPos))
            this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
        else
            null
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)
        if (!level.isClientSide) {
            val dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING)
            val newState = ModBlocks.workstationChild.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, dir)
            val blockpos = getPos(dir, pos)
            level.setBlock(blockpos, newState, UPDATE_ALL)
            level.blockUpdated(pos, Blocks.AIR)
            newState.updateNeighbourShapes(level, pos, UPDATE_ALL)
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING).add(HAS_TAPE)
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> = codec

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = WorkstationBE(pos, state)

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape {
        return when(state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction.NORTH -> NORTH_SHAPE
            Direction.SOUTH -> SOUTH_SHAPE
            Direction.WEST -> WEST_SHAPE
            Direction.EAST -> EAST_SHAPE
            else -> NORTH_SHAPE
        }
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!level.isClientSide && player.isCreative) {
            val dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING)

            val blockpos = getPos(dir, pos)

            val blockstate = level.getBlockState(blockpos)
            if (blockstate.`is`(ModBlocks.workstationChild) && state.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir) {
                level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35)
                level.levelEvent(player, 2001, blockpos, getId(blockstate))
            }
        }

        return super.playerWillDestroy(level, pos, state, player)
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

    override fun getDrops(state: BlockState, params: LootParams.Builder): List<ItemStack> {
        val drops = super.getDrops(state, params).toMutableList()
        val blockE = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockE is WorkstationBE && !blockE.inventory[0].isEmpty) {
            drops.add(blockE.inventory[0]);
        }
        return drops.toList()
    }
}

private inline val Number.px: Double get() = this.toDouble() / 16.0
