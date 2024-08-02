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
        poseStack.mulPose(Axis.YN.rotationDegrees(entity.yRot))
        poseStack.translate(width / -2.0, finalHeight / 2.0 - height / 2.0 + 0.01, depth / -2.0)
        if (width >= 0) renderTimedoor(poseStack, buffer, width, height, depth, entity.color.value, entity.tickCount)
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
        age: Int,
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
            .addVertex(model, minX, maxY, maxZ).color().setUv(maxZ, maxY).setUv2(1, 1)
            .addVertex(model, minX, maxY, minZ).color().setUv(minZ, maxY).setUv2(0, 1)
            .addVertex(model, minX, minY, minZ).color().setUv(minZ, minY).setUv2(0, 0)
            .addVertex(model, minX, minY, maxZ).color().setUv(maxZ, minY).setUv2(1, 0)
            //Right
            .addVertex(model, maxX, maxY, minZ).color().setUv(minZ, maxY).setUv2(0, 1)
            .addVertex(model, maxX, maxY, maxZ).color().setUv(maxZ, maxY).setUv2(1, 1)
            .addVertex(model, maxX, minY, maxZ).color().setUv(maxZ, minY).setUv2(1, 0)
            .addVertex(model, maxX, minY, minZ).color().setUv(minZ, minY).setUv2(0, 0)

        val lineBuffer = multiBufferSource.getBuffer(RenderType.lines())

        val widthPart = width * 20f
        val heightPart = height * 20f
        val total = (widthPart * 2 + heightPart * 2)
        val widthLine = (total / widthPart) * .0625f
        val heightLine = (total / heightPart) * .0625f
        val topPercent: Float
        val rightPercent: Float
        val bottomPercent: Float
        val leftPercent: Float
        if (widthPart > heightPart) {
            topPercent = 1 - (((age + widthPart) % total) - widthPart) / (widthPart - (widthPart * widthLine * 1.5f)) + widthLine
            rightPercent = 1 - (age % total - widthPart) / heightPart
            bottomPercent = 1 - (age % total - widthPart - heightPart) / (widthPart - (widthPart * widthLine * 1.5f)) + widthLine
            leftPercent = 1 - ((age - widthPart) % total - widthPart - heightPart) / heightPart
        } else {
            topPercent = 1 - (((age + widthPart) % total) - widthPart) / widthPart
            rightPercent = 1 - (((age + widthPart) % total) - widthPart * 2) / (heightPart - (heightPart * heightLine * 1.5f)) + heightLine
            bottomPercent = 1 - (age % total - widthPart - heightPart) / widthPart
            leftPercent = 1 - ((age - widthPart) % total - widthPart - heightPart) / (heightPart - (heightPart * heightLine * 1.5f)) + heightLine
        }


        //front
        lineBuffer.addVertex(model, minX, maxY, minZ).color().setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
        if (topPercent > -widthLine && topPercent < (1 + widthLine)) {
            val start = Mth.clamp(topPercent - widthLine, 0f, 1f)
            val end = Mth.clamp(topPercent + widthLine, 0f, 1f)
            val middle = Mth.clamp(topPercent, 0f, 1f)

            lineBuffer.addVertex(model, Mth.lerp(start, 0f, maxX), maxY, minZ)
                .color()
                .setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(start, 0f, maxX), maxY, minZ)
                .color()
                .setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(middle, 0f, maxX), maxY, minZ)
                .setColor(0xFFFFFFFF.toInt())
                .setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(middle, 0f, maxX), maxY, minZ)
                .setColor(0xFFFFFFFF.toInt())
                .setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(end, 0f, maxX), maxY, minZ)
                .color()
                .setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(end, 0f, maxX), maxY, minZ)
                .color()
                .setNormal(matrix3f, -1.0F, 0.0F, 0.0F)
        }
        lineBuffer.addVertex(model, maxX, maxY, minZ).color().setNormal(matrix3f, -1.0F, 0.0F, 0.0F)


        lineBuffer.addVertex(model, minX, minY, minZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
        if (rightPercent > -heightLine && rightPercent < (1 + heightLine)) {
            val start = Mth.clamp(rightPercent - heightLine, 0f, 1f)
            val end = Mth.clamp(rightPercent + heightLine, 0f, 1f)
            val middle = Mth.clamp(rightPercent, 0f, 1f)

            lineBuffer.addVertex(model, minX, Mth.lerp(start, 0f, maxY), minZ)
                .color()
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, minX, Mth.lerp(start, 0f, maxY), minZ)
                .color()
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, minX, Mth.lerp(middle, 0f, maxY), minZ)
                .setColor(0xFFFFFFFF.toInt())
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, minX, Mth.lerp(middle, 0f, maxY), minZ)
                .setColor(0xFFFFFFFF.toInt())
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, minX, Mth.lerp(end, 0f, maxY), minZ)
                .color()
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, minX, Mth.lerp(end, 0f, maxY), minZ)
                .color()
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
        }
        lineBuffer.addVertex(model, minX, maxY, minZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)


        lineBuffer.addVertex(model, minX, minY, minZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F);
        if (bottomPercent > -widthLine && bottomPercent < (1 + widthLine)) {
            val start = Mth.clamp(bottomPercent + widthLine, 0f, 1f)
            val end = Mth.clamp(bottomPercent - widthLine, 0f, 1f)
            val middle = Mth.clamp(bottomPercent, 0f, 1f)

            lineBuffer.addVertex(model, Mth.lerp(1 - start, 0f, maxX), minY, minZ)
                .color()
                .setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(1 - start, 0f, maxX), minY, minZ)
                .color()
                .setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(1 - middle, 0f, maxX), minY, minZ)
                .setColor(0xFFFFFFFF.toInt())
                .setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(1 - middle, 0f, maxX), minY, minZ)
                .setColor(0xFFFFFFFF.toInt())
                .setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(1 - end, 0f, maxX), minY, minZ)
                .color()
                .setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            lineBuffer.addVertex(model, Mth.lerp(1 - end, 0f, maxX), minY, minZ)
                .color()
                .setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
        }
        lineBuffer.addVertex(model, maxX, minY, minZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)


        lineBuffer.addVertex(model, maxX, maxY, minZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
        if (leftPercent > -heightLine && leftPercent < (1 + heightLine)) {
            val start = Mth.clamp(leftPercent - heightLine, 0f, 1f)
            val end = Mth.clamp(leftPercent + heightLine, 0f, 1f)
            val middle = Mth.clamp(leftPercent, 0f, 1f)

            lineBuffer.addVertex(model, maxX, Mth.lerp(start, maxY, 0f), minZ)
                .color()
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, maxX, Mth.lerp(start, maxY, 0f), minZ)
                .color()
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, maxX, Mth.lerp(middle, maxY, 0f), minZ)
                .setColor(0xFFFFFFFF.toInt())
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, maxX, Mth.lerp(middle, maxY, 0f), minZ)
                .setColor(0xFFFFFFFF.toInt())
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, maxX, Mth.lerp(end, maxY, 0f), minZ)
                .color()
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            lineBuffer.addVertex(model, maxX, Mth.lerp(end, maxY, 0f), minZ)
                .color()
                .setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
        }
        lineBuffer.addVertex(model, maxX, minY, minZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)

            // connecting front and back
            .addVertex(model, minX, minY, minZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
            .addVertex(model, minX, minY, maxZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)

            .addVertex(model, maxX, minY, minZ).color().setNormal(matrix3f, 0.0F, 0.0F, -1.0F)
            .addVertex(model, maxX, minY, maxZ).color().setNormal(matrix3f, 0.0F, 0.0F, -1.0F)

            .addVertex(model, minX, maxY, minZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
            .addVertex(model, minX, maxY, maxZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)

            .addVertex(model, maxX, maxY, minZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)
            .addVertex(model, maxX, maxY, maxZ).color().setNormal(matrix3f, 0.0F, 0.0F, 1.0F)

            //back
            .addVertex(model, minX, maxY, maxZ).color().setNormal(matrix3f, 0.0F, -1.0F, 0.0F)
            .addVertex(model, minX, minY, maxZ).color().setNormal(matrix3f, 0.0F, -1.0F, 0.0F)
            .addVertex(model, minX, minY, maxZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, maxX, minY, maxZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, maxX, minY, maxZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            .addVertex(model, maxX, maxY, maxZ).color().setNormal(matrix3f, 0.0F, 1.0F, 0.0F)
            .addVertex(model, maxX, maxY, maxZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
            .addVertex(model, minX, maxY, maxZ).color().setNormal(matrix3f, 1.0F, 0.0F, 0.0F)
    }
}