package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class LiftwayBlock(props: Properties): BaseEntityBlock(props) {
    companion object {
        val shape = Shapes.or(
            box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            box(3.0, 3.0, 3.0, 13.0, 4.0, 13.0)
        )
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (!level.isClientSide) return null
        return createTickerHelper(type, ModBlocks.liftwayBE) { _, _, _, block -> block.clientTick() }
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape {
        return shape
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = simpleCodec(::LiftwayBlock)

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = LiftwayBe(pos, state)
}