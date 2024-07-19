package earth.terrarium.tempad.client.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.client.TempadClient
import earth.terrarium.tempad.common.entity.TimedoorEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.joml.Matrix4f


class TimedoorRenderer(ctx: EntityRendererProvider.Context) : EntityRenderer<TimedoorEntity>(ctx) {
    override fun getTextureLocation(pEntity: TimedoorEntity): ResourceLocation = "".tempadId

    override fun render(
        entity: TimedoorEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
    ) {
        var width = 0.0625f * 20
        val finalHeight = 0.0625f * 36
        var height = finalHeight
        val depth = 0.0625f * 6
        val closingTime: Int = entity.closingTime
        val tickLength = TimedoorEntity.ANIMATION_LENGTH
        val phaseLength = (tickLength) / 2
        val ticks = entity.tickCount
        val animation: Float = (ticks + partialTick) / tickLength

        if (ticks < phaseLength) {
            width = Mth.lerp(animation * 2, 0f, width)
            height = .2f
        }

        if (ticks in phaseLength..<tickLength) {
            height = Mth.lerp((animation - 0.5f) * 2, .2f, height)
        }

        if (closingTime != -1) {
            if (ticks > closingTime && ticks < closingTime + phaseLength) {
                height = Mth.lerp(1 - (animation - closingTime.toFloat() / tickLength) * 2, .2f, height)
            }

            if (ticks >= closingTime + phaseLength) {
                width = Mth.lerp(1 - (animation - closingTime.toFloat() / tickLength - 0.5f) * 2, 0f, width)
                height = .2f
            }
        }

        poseStack.pushPose()
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.yRot))
        poseStack.translate(width / -2.0, finalHeight / 2.0 - height / 2.0 + 0.01, depth / -2.0)
        if (width >= 0) renderTimedoor(poseStack, buffer, width, height, depth, entity.color.value)
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight)
        poseStack.popPose()
    }

    fun renderTimedoor(
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource,
        width: Float,
        height: Float,
        depth: Float,
        color: Int,
    ) {
        val maxX = width
        val maxY = height
        val maxZ = depth
        val minX = 0f
        val minY = 0f
        val minZ = 0f

        val model: Matrix4f = poseStack.last().pose()
        val matrix3f = poseStack.last()
        val buffer = multiBufferSource.getBuffer(TempadClient.renderType)

        //Front
        val red = ((color and 0xFF0000) shr 16) / 255.0f
        val green = ((color and 0xFF00) shr 8) / 255.0f
        val blue = (color and 0xFF) / 255.0f

        fun VertexConsumer.color() = setColor(red, green, blue, 1f)

        buffer
            //Front
            .addVertex(model, minX, maxY, minZ).color().setUv(minX, maxY).setUv2(0, 1)
            .addVertex(model, maxX, maxY, minZ).color().setUv(maxX, maxY).setUv2(1, 1)
            .addVertex(model, maxX, minY, minZ).color().setUv(maxX, minY).setUv2(1, 0)
            .addVertex(model, minX, minY, minZ).color().setUv(minX, minY).setUv2(0, 0)
            //Back
            .addVertex(model, maxX, maxY, maxZ).color().setUv(maxX, maxY).setUv2(1, 1)
            .addVertex(model, minX, maxY, maxZ).color().setUv(minX, maxY).setUv2(0, 1)
            .addVertex(model, minX, minY, maxZ).color().setUv(minX, minY).setUv2(0, 0)
            .addVertex(model, maxX, minY, maxZ).color().setUv(maxX, minY).setUv2(1, 0)
            //Top
            .addVertex(model, minX, maxY, maxZ).color().setUv(minX, maxZ).setUv2(0, 1)
            .addVertex(model, maxX, maxY, maxZ).color().setUv(maxX, maxZ).setUv2(1, 1)
            .addVertex(model, maxX, maxY, minZ).color().setUv(maxX, minZ).setUv2(1, 0)
            .addVertex(model, minX, maxY, minZ).color().setUv(minX, minZ).setUv2(0, 0)
            //Bottom
            .addVertex(model, minX, minY, minZ).color().setUv(minX, minZ).setUv2(0, 0)
            .addVertex(model, maxX, minY, minZ).color().setUv(maxX, minZ).setUv2(1, 0)
            .addVertex(model, maxX, minY, maxZ).color().setUv(maxX, maxZ).setUv2(1, 1)
            .addVertex(model, minX, minY, maxZ).color().setUv(minX, maxZ).setUv2(0, 1)
            //Left
            .addVertex(model, minX, maxY, maxZ).color().setUv(maxZ, maxY).setUv2( 1, 1)
            .addVertex(model, minX, maxY, minZ).color().setUv(minZ, maxY).setUv2( 0, 1)
            .addVertex(model, minX, minY, minZ).color().setUv(minZ, minY).setUv2( 0, 0)
            .addVertex(model, minX, minY, maxZ).color().setUv(maxZ, minY).setUv2( 1, 0)
            //Right
            .addVertex(model, maxX, maxY, minZ).color().setUv(minZ, maxY).setUv2( 0, 1)
            .addVertex(model, maxX, maxY, maxZ).color().setUv(maxZ, maxY).setUv2( 1, 1)
            .addVertex(model, maxX, minY, maxZ).color().setUv(maxZ, minY).setUv2( 1, 0)
            .addVertex(model, maxX, minY, minZ).color().setUv(minZ, minY).setUv2( 0, 0)

        val lineBuffer = multiBufferSource.getBuffer(RenderType.lines())

        lineBuffer
            .addVertex(model, minX, minY, minZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, maxX, minY, minZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, minX, minY, minZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            .addVertex(model, minX, maxY, minZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            .addVertex(model, minX, minY, minZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
            .addVertex(model, minX, minY, maxZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
            .addVertex(model, maxX, minY, minZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            .addVertex(model, maxX, maxY, minZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            .addVertex(model, maxX, maxY, minZ).color().setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
            .addVertex(model, minX, maxY, minZ).color().setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
            .addVertex(model, minX, maxY, minZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
            .addVertex(model, minX, maxY, maxZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
            .addVertex(model, minX, maxY, maxZ).color().setNormal(matrix3f, 0.0F, -1.0F, 0.0F)
            .addVertex(model, minX, minY, maxZ).color().setNormal(matrix3f, 0.0F, -1.0F, 0.0F)
            .addVertex(model, minX, minY, maxZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, maxX, minY, maxZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, maxX, minY, maxZ).color().setNormal(matrix3f, 0.0F, 0.0F, -1.0F)
            .addVertex(model, maxX, minY, minZ).color().setNormal(matrix3f, 0.0F, 0.0F, -1.0F)
            .addVertex(model, minX, maxY, maxZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, maxX, maxY, maxZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, maxX, minY, maxZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            .addVertex(model, maxX, maxY, maxZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            .addVertex(model, maxX, maxY, minZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
            .addVertex(model, maxX, maxY, maxZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
    }
}