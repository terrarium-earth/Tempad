package earth.terrarium.tempad.client.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import earth.terrarium.tempad.client.clientLevel
import earth.terrarium.tempad.common.block.WorkstationBE
import it.unimi.dsi.fastutil.HashCommon
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3

data class WorkstationRenderState(var item: ItemStackRenderState = ItemStackRenderState(), var blockState: BlockState? = null) : BlockEntityRenderState()

class WorkstationRenderer(val itemModelResolver: ItemModelResolver) : BlockEntityRenderer<WorkstationBE, WorkstationRenderState> {

    override fun createRenderState(): WorkstationRenderState = WorkstationRenderState()

    override fun extractRenderState(
        block: WorkstationBE,
        state: WorkstationRenderState,
        partialTick: Float,
        cameraPos: Vec3,
        crumbling: ModelFeatureRenderer.CrumblingOverlay?,
    ) {
        BlockEntityRenderState.extractBase(block, state, crumbling)
        val item = block.inventory.getResource(0).toStack(block.inventory.getAmountAsInt(0))
        val seed = HashCommon.long2int(block.getBlockPos().asLong())
        itemModelResolver.updateForTopItem(state.item, item, ItemDisplayContext.FIXED, block.level, block, seed)
        state.blockState = block.blockState
    }

    override fun submit(
        state: WorkstationRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        cameraState: CameraRenderState,
    ) {
        val item = state.item
        if (item.isEmpty) return

        poseStack.pushPose()
        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.mulPose(Axis.XP.rotationDegrees(90f))
        val blockState = state.blockState
        if (blockState != null) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90f))
        }
        poseStack.translate((-1.5).px, (-1).px, (-2.5).px)
        poseStack.mulPose(Axis.ZN.rotationDegrees(90f))
        poseStack.scale(12.px, 12.px, 12.px)
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0)
        poseStack.popPose()
    }
}

private inline val Number.px: Float get() = this.toFloat() / 16f
