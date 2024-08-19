package earth.terrarium.tempad.client.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import earth.terrarium.tempad.client.ShaderModBridge
import earth.terrarium.tempad.tempadId
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
        val tickLength = TimedoorEntity.ANIMATION_LENGTH
        val animation: Float
        val ticks = entity.tickCount + partialTick

        if (entity.closingTime < ticks) {
            animation = Mth.clamp(1 - (ticks - entity.closingTime) / tickLength.toFloat(), 0f, 1f)
        } else {
            animation = Mth.clamp(ticks / tickLength.toFloat(), 0f, 1f)
        }

        val width = entity.sizing.widthAtPercent(animation)
        val height = entity.sizing.heightAtPercent(animation)
        val depth = entity.sizing.depthAtPercent(animation)
        val finalHeight = entity.sizing.dimensions.height

        poseStack.pushPose()
        poseStack.mulPose(Axis.YN.rotationDegrees(entity.yRot))
        poseStack.translate(width / -2.0, finalHeight / 2.0 - height / 2.0 + 0.01, depth / -2.0)
        if (width >= 0) renderTimedoor(poseStack, buffer, width, height, depth, entity.color.value, packedLight, entity.tickCount, entity.sizing.showLineAnimation)
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
        packedLight: Int,
        age: Int,
        animate: Boolean = true
    ) {
        val maxX = width
        val maxY = height
        val maxZ = depth
        val minX = 0f
        val minY = 0f
        val minZ = 0f

        val model: Matrix4f = poseStack.last().pose()
        val matrix3f = poseStack.last()
        val buffer = multiBufferSource.getBuffer(if(ShaderModBridge.shadersEnabled) RenderType.textBackgroundSeeThrough() else TempadClient.renderType)

        //Front
        val red = ((color and 0xFF0000) shr 16) / 255.0f
        val green = ((color and 0xFF00) shr 8) / 255.0f
        val blue = (color and 0xFF) / 255.0f

        fun VertexConsumer.color(opacity: Float = 1f) = setColor(red, green, blue, opacity)

        buffer
            //Front
            .addVertex(model, minX, maxY, minZ).color(.4f).setUv(minX, maxY).setUv2(0, 1).setLight(packedLight)
            .addVertex(model, maxX, maxY, minZ).color(.4f).setUv(maxX, maxY).setUv2(1, 1).setLight(packedLight)
            .addVertex(model, maxX, minY, minZ).color(.4f).setUv(maxX, minY).setUv2(1, 0).setLight(packedLight)
            .addVertex(model, minX, minY, minZ).color(.4f).setUv(minX, minY).setUv2(0, 0).setLight(packedLight)
            //Back
            .addVertex(model, maxX, maxY, maxZ).color(.4f).setUv(maxX, maxY).setUv2(1, 1).setLight(packedLight)
            .addVertex(model, minX, maxY, maxZ).color(.4f).setUv(minX, maxY).setUv2(0, 1).setLight(packedLight)
            .addVertex(model, minX, minY, maxZ).color(.4f).setUv(minX, minY).setUv2(0, 0).setLight(packedLight)
            .addVertex(model, maxX, minY, maxZ).color(.4f).setUv(maxX, minY).setUv2(1, 0).setLight(packedLight)
            //Top
            .addVertex(model, minX, maxY, maxZ).color(.4f).setUv(minX, maxZ).setUv2(0, 1).setLight(packedLight)
            .addVertex(model, maxX, maxY, maxZ).color(.4f).setUv(maxX, maxZ).setUv2(1, 1).setLight(packedLight)
            .addVertex(model, maxX, maxY, minZ).color(.4f).setUv(maxX, minZ).setUv2(1, 0).setLight(packedLight)
            .addVertex(model, minX, maxY, minZ).color(.4f).setUv(minX, minZ).setUv2(0, 0).setLight(packedLight)
            //Bottom
            .addVertex(model, minX, minY, minZ).color(.4f).setUv(minX, minZ).setUv2(0, 0).setLight(packedLight)
            .addVertex(model, maxX, minY, minZ).color(.4f).setUv(maxX, minZ).setUv2(1, 0).setLight(packedLight)
            .addVertex(model, maxX, minY, maxZ).color(.4f).setUv(maxX, maxZ).setUv2(1, 1).setLight(packedLight)
            .addVertex(model, minX, minY, maxZ).color(.4f).setUv(minX, maxZ).setUv2(0, 1).setLight(packedLight)
            //Left
            .addVertex(model, minX, maxY, maxZ).color(.4f).setUv(maxZ, maxY).setUv2(1, 1).setLight(packedLight)
            .addVertex(model, minX, maxY, minZ).color(.4f).setUv(minZ, maxY).setUv2(0, 1).setLight(packedLight)
            .addVertex(model, minX, minY, minZ).color(.4f).setUv(minZ, minY).setUv2(0, 0).setLight(packedLight)
            .addVertex(model, minX, minY, maxZ).color(.4f).setUv(maxZ, minY).setUv2(1, 0).setLight(packedLight)
            //Right
            .addVertex(model, maxX, maxY, minZ).color(.4f).setUv(minZ, maxY).setUv2(0, 1).setLight(packedLight)
            .addVertex(model, maxX, maxY, maxZ).color(.4f).setUv(maxZ, maxY).setUv2(1, 1).setLight(packedLight)
            .addVertex(model, maxX, minY, maxZ).color(.4f).setUv(maxZ, minY).setUv2(1, 0).setLight(packedLight)
            .addVertex(model, maxX, minY, minZ).color(.4f).setUv(minZ, minY).setUv2(0, 0).setLight(packedLight)

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
        if (animate && topPercent > -widthLine && topPercent < (1 + widthLine)) {
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
        if (animate && rightPercent > -heightLine && rightPercent < (1 + heightLine)) {
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
        if (animate && bottomPercent > -widthLine && bottomPercent < (1 + widthLine)) {
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
        if (animate && leftPercent > -heightLine && leftPercent < (1 + heightLine)) {
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