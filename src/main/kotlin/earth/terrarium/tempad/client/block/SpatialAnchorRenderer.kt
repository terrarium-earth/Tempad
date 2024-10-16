package earth.terrarium.tempad.client.block

import com.mojang.blaze3d.vertex.PoseStack
import earth.terrarium.tempad.common.block.SpatialAnchorBE
import earth.terrarium.tempad.common.registries.color
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.util.RandomSource
import net.neoforged.neoforge.client.RenderTypeHelper
import net.neoforged.neoforge.client.model.data.ModelData

class SpatialAnchorRenderer(val blockRenderer: BlockRenderDispatcher): BlockEntityRenderer<SpatialAnchorBE> {
    override fun render(
        blockEntity: SpatialAnchorBE,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
    ) {
        val state = blockEntity.blockState
        val bakedmodel = blockRenderer.getBlockModel(state)
        val color = blockEntity.color
        val renderTypes = bakedmodel.getRenderTypes(state, RandomSource.create(42), ModelData.EMPTY)

        for (rt in renderTypes) {
            blockRenderer.modelRenderer.renderModel(
                    poseStack.last(),
                    bufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)),
                    state,
                    bakedmodel,
                    color.floatRed,
                    color.floatGreen,
                    color.floatBlue,
                    packedLight,
                    packedOverlay,
                    ModelData.EMPTY,
                    rt
            )
        }
    }
}