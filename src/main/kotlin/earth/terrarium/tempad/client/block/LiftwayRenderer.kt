package earth.terrarium.tempad.client.block

import com.mojang.blaze3d.vertex.PoseStack
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.sizing.FloorPlacementSettings
import earth.terrarium.tempad.client.entity.TimedoorRenderer
import earth.terrarium.tempad.common.block.LiftwayBe
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.block.BlockModelResolver
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.item.properties.numeric.Count
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3

class LiftwayRenderer(val blockModelResolver: BlockModelResolver): BlockEntityRenderer<LiftwayBe, LiftwayRenderState> {
    companion object {
        val placement = FloorPlacementSettings()
    }

    override fun shouldRenderOffScreen(): Boolean = true

    override fun submit(
        state: LiftwayRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        cameraRenderState: CameraRenderState,
    ) {
        if (state.tickCount <= 0) return

        val width: Float
        val height: Float
        val depth: Float

        val zOffset: Double
        if (state.tickCount <= 7) {
            width = placement.widthAtPercent(1f)
            height = placement.heightAtPercent(1f)
            depth = placement.depthAtPercent(1f)
            zOffset = Mth.lerp(state.tickCount / 7.0, 0.0, 2.25)
        } else if (state.tickCount > 7 && state.tickCount <= 12) {
            width = placement.widthAtPercent(Mth.clamp(1f - (state.tickCount - 7) / 5f, 0f, 1f))
            height = placement.heightAtPercent(Mth.clamp(1f - (state.tickCount - 7) / 5f, 0f, 1f))
            depth = placement.depthAtPercent(Mth.clamp(1f - (state.tickCount - 7) / 5f, 0f, 1f))
            zOffset = 2.25
        } else {
            return
        }
        poseStack.pushPose()
        poseStack.translate((1.0 - width) / 2, zOffset, (1.0 - depth) / 2)
        TimedoorRenderer.renderTimedoor(placement, poseStack, submitNodeCollector, width, height, depth, Tempad.ORANGE, state.tickCount)
        poseStack.popPose()
    }

    override fun createRenderState(): LiftwayRenderState = LiftwayRenderState()

    override fun extractRenderState(
        blockEntity: LiftwayBe,
        state: LiftwayRenderState,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?,
    ) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)

        if (blockEntity.usageCount > 0) {
            state.tickCount = blockEntity.tickCount + partialTicks
        }
    }
}

class LiftwayRenderState(var tickCount: Float = 0f): BlockEntityRenderState()