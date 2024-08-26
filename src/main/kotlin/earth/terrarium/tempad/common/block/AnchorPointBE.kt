package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.common.registries.ModAttachments
import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.jvm.optionals.getOrNull

class AnchorPointBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.anchorPointBE, pos, state) {
    var name: Component get() = getExistingData(ModAttachments.name).getOrNull() ?: ModBlocks.anchorPoint.name
        set(value) {
            setData(ModAttachments.name, value)
        }
}