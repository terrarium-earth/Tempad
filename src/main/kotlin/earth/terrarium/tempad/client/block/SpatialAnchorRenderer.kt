package earth.terrarium.tempad.client.block

import com.mojang.blaze3d.vertex.PoseStack
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBe
import earth.terrarium.tempad.common.registries.color
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.block.BlockModelRenderState
import net.minecraft.client.renderer.block.BlockModelResolver
import net.minecraft.client.renderer.block.model.BlockDisplayContext
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ARGB
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class SpatialAnchorRenderState : BlockEntityRenderState() {
    var modelState = BlockModelRenderState()
}

class SpatialAnchorRenderer(val blockModelResolver: BlockModelResolver) : BlockEntityRenderer<AbstractMarkerBe, SpatialAnchorRenderState> {
    companion object {
        val ctx = BlockDisplayContext.create()
    }

    override fun createRenderState(): SpatialAnchorRenderState = SpatialAnchorRenderState()

    override fun extractRenderState(
        blockEntity: AbstractMarkerBe,
        state: SpatialAnchorRenderState,
        partialTick: Float,
        cameraPos: Vec3,
        crumbling: ModelFeatureRenderer.CrumblingOverlay?,
    ) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumbling)
        blockModelResolver.update(state.modelState, blockEntity.blockState, ctx)
        if (state.modelState.tintLayers().size > 0) {
            state.modelState.tintLayers().set(0, blockEntity.color.value)
        } else {
            state.modelState.tintLayers().add(blockEntity.color.value)
        }
    }

    override fun submit(
        state: SpatialAnchorRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        cameraState: CameraRenderState,
    ) {
        state.modelState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0)
    }
}
