package earth.terrarium.tempad.client.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import earth.terrarium.tempad.common.block.WorkstationBE
import earth.terrarium.tempad.common.block.WorkstationBlock
import earth.terrarium.tempad.common.utils.get
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class WorkstationRenderer(val itemRenderer: ItemRenderer): BlockEntityRenderer<WorkstationBE> {
    override fun render(
        block: WorkstationBE,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
    ) {
        val item = block.inventory[0]
        if(item.isEmpty) return

        poseStack.pushPose()
        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.mulPose(Axis.XP.rotationDegrees(90f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(block.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90f))
        poseStack.translate((-1.5).px, (-1).px, (-2.5).px)
        poseStack.mulPose(Axis.ZN.rotationDegrees(90f))
        poseStack.scale(12.px, 12.px, 12.px)
        itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, block.level, 0)
        poseStack.popPose()
    }
}

private inline val Number.px: Float get() = this.toFloat() / 16f
