package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class RudimentaryTempadBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.rudimentaryTempadBE, pos, state) {
}