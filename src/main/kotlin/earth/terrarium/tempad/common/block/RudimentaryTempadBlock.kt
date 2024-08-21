package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class RudimentaryTempadBlock: BaseEntityBlock(Properties.of()) {
    companion object {
        val codec: MapCodec<out BaseEntityBlock> = simpleCodec { ModBlocks.rudimentaryTempad }
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = codec
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = RudimentaryTempadBE(pos, state)
}