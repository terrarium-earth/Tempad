package earth.terrarium.tempad.common.block.timedoor_marker

import com.mojang.serialization.MapCodec
import earth.terrarium.tempad.common.registries.ModAttachments
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class TimedoorMarkerBlock(props: Properties): AbstractMarkerBlock(props, ModAttachments::basicDoorpoints) {
    companion object {
        val codec = simpleCodec(::TimedoorMarkerBlock)
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> = codec

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = TimedoorMarkerBE(pos, state)
}