package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class AnchorPointBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.anchorPointBE, pos, state) {
    val name: Component = components().get(DataComponents.CUSTOM_NAME) ?: ModBlocks.anchorPoint.name
}