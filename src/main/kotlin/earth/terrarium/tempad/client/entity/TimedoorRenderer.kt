package earth.terrarium.tempad.client.entity

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.api.sizing.TimedoorPlacementSettings
import earth.terrarium.tempad.client.ShaderModBridge
import earth.terrarium.tempad.client.TempadClient
import earth.terrarium.tempad.client.clientLevel
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.tempadId
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderBuffers
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState
import org.joml.Matrix4f
import org.joml.Vector2i

class TimedoorRenderer(val ctx: EntityRendererProvider.Context) : EntityRenderer<TimedoorEntity, TimedoorRenderState>(ctx) {
    companion object {
        private val faceTextures = hashMapOf<Pair<TimedoorPlacementSettings, BoxFace>, Identifier>()
    }

    override fun submit(
        entity: TimedoorRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val ticks = entity.openingTime
        if (ticks < TimedoorEntity.IDLE_BEFORE_START) return
        val tickLength = TimedoorEntity.ANIMATION_LENGTH
        val animation: Float

        if (entity.closingTime < TimedoorEntity.ANIMATION_LENGTH) {
            animation = Mth.clamp(1 - (ticks - entity.beganClosing) / tickLength.toFloat(), 0f, 1f)
        } else {
            animation = Mth.clamp((ticks - TimedoorEntity.IDLE_BEFORE_START) / tickLength.toFloat(), 0f, 1f)
        }

        val width = entity.placementSettings.widthAtPercent(animation)
        val height = entity.placementSettings.heightAtPercent(animation)
        val depth = entity.placementSettings.depthAtPercent(animation)
        val finalHeight = entity.placementSettings.dimensions.height

        poseStack.pushPose()
        poseStack.mulPose(Axis.YN.rotationDegrees(entity.yRot + 180))
        poseStack.translate(width / -2.0, finalHeight / 2.0 - height / 2.0 + 0.01, depth / -2.0)
        if (entity.isUnstable && ticks % 65 > 60) {
            val randomX = Mth.randomBetween(clientLevel?.random!!, -0.05f, 0.05f)
            val randomY = Mth.randomBetween(clientLevel?.random!!, -0.05f, 0.05f)
            val randomZ = Mth.randomBetween(clientLevel?.random!!, -0.05f, 0.05f)
            poseStack.translate(randomX, randomY, randomZ)
        }

        if (width > 0) renderTimedoor(
            entity.placementSettings,
            poseStack,
            Minecraft.getInstance().renderBuffers().bufferSource(),
            width,
            height,
            depth,
            entity.color.value,
            ticks,
            entity.placementSettings.showLineAnimation
        )
        super.submit(entity, poseStack, submitNodeCollector, camera)
        poseStack.popPose()
    }

    fun TimedoorPlacementSettings.texture(face: BoxFace): NativeImage {
        val (textureWidth, textureHeight) = face.getDimensions(this)
        return NativeImage(textureWidth, textureHeight, true).apply {
            repeat(textureWidth) { x ->
                repeat(textureHeight) { y ->
                    val x2: Float = x * 2f / textureWidth - 1
                    val y2: Float = y * 2f / textureHeight - 1
                    val alpha = (x2 * x2 / 3 + y2 * y2 / 3)
                    val modAlpha = alpha - (alpha.mod(0.07))
                    setPixel(x, y, ((modAlpha * 255).toInt() + 26) shl 24 or 0xFFFFFF)
                }
            }
        }
    }

    fun registerFaceTexture(sizing: TimedoorPlacementSettings, face: BoxFace): Identifier? {
        if (!ShaderModBridge.shadersEnabled) return null
        return faceTextures.computeIfAbsent(sizing to face) { _ ->
            val id = "${sizing.type.id.path} + __ + ${face.name.lowercase()}".tempadId
            Minecraft.getInstance().textureManager.register(
                id,
                DynamicTexture(id::toString, sizing.texture(face))
            )
            return@computeIfAbsent id
        }
    }

    fun renderTimedoor(
        sizing: TimedoorPlacementSettings,
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource,
        width: Float,
        height: Float,
        depth: Float,
        color: Int,
        age: Float,
        animate: Boolean = true,
    ) {
        val maxX = width
        val maxY = height
        val maxZ = depth
        val minX = 0f
        val minY = 0f
        val minZ = 0f

        val model: Matrix4f = poseStack.last().pose()
        val matrix3f = poseStack.last()

        //Front
        val red = ((color shr 16) and 0xFF) / 255.0f
        val green = ((color shr 8) and 0xFF) / 255.0f
        val blue = (color and 0xFF) / 255.0f

        fun VertexConsumer.color() = setColor(red, green, blue, 1f)
        fun VertexConsumer.size(u: Float, v: Float): VertexConsumer {
            if (!ShaderModBridge.shadersEnabled) {
                setUv2((u * 16).toInt(), (v * 16).toInt())
            }
            return this
        }

        var buffer = multiBufferSource.getBuffer(TempadClient.renderType(registerFaceTexture(sizing, BoxFace.FrontBack)))
        buffer
            //Front
            .addVertex(model, minX, maxY, minZ).color().setUv(0f, 1f).size(minX, maxY)
            .addVertex(model, maxX, maxY, minZ).color().setUv(1f, 1f).size(maxX, maxY)
            .addVertex(model, maxX, minY, minZ).color().setUv(1f, 0f).size(maxX, minY)
            .addVertex(model, minX, minY, minZ).color().setUv(0f, 0f).size(minX, minY)
            //Back
            .addVertex(model, maxX, maxY, maxZ).color().setUv(1f, 1f).size(maxX, maxY)
            .addVertex(model, minX, maxY, maxZ).color().setUv(0f, 1f).size(minX, maxY)
            .addVertex(model, minX, minY, maxZ).color().setUv(0f, 0f).size(minX, minY)
            .addVertex(model, maxX, minY, maxZ).color().setUv(1f, 0f).size(maxX, minY)

        if(ShaderModBridge.shadersEnabled) {
            buffer = multiBufferSource.getBuffer(TempadClient.renderType(registerFaceTexture(sizing, BoxFace.TopBottom)))
        }

        buffer
            //Top
            .addVertex(model, minX, maxY, maxZ).color().setUv(0f, 1f).size(minX, maxZ)
            .addVertex(model, maxX, maxY, maxZ).color().setUv(1f, 1f).size(maxX, maxZ)
            .addVertex(model, maxX, maxY, minZ).color().setUv(1f, 0f).size(maxX, minZ)
            .addVertex(model, minX, maxY, minZ).color().setUv(0f, 0f).size(minX, minZ)
            //Bottom
            .addVertex(model, minX, minY, minZ).color().setUv(0f, 0f).size(minX, minZ)
            .addVertex(model, maxX, minY, minZ).color().setUv(1f, 0f).size(maxX, minZ)
            .addVertex(model, maxX, minY, maxZ).color().setUv(1f, 1f).size(maxX, maxZ)
            .addVertex(model, minX, minY, maxZ).color().setUv(0f, 1f).size(minX, maxZ)

        if(ShaderModBridge.shadersEnabled) {
            buffer = multiBufferSource.getBuffer(TempadClient.renderType(registerFaceTexture(sizing, BoxFace.LeftRight)))
        }

        buffer
            //Left
            .addVertex(model, minX, maxY, maxZ).color().setUv(1f, 1f).size(maxZ, maxY)
            .addVertex(model, minX, maxY, minZ).color().setUv(0f, 1f).size(minZ, maxY)
            .addVertex(model, minX, minY, minZ).color().setUv(0f, 0f).size(minZ, minY)
            .addVertex(model, minX, minY, maxZ).color().setUv(1f, 0f).size(maxZ, minY)
            //Right
            .addVertex(model, maxX, maxY, minZ).color().setUv(0f, 1f).size(minZ, maxY)
            .addVertex(model, maxX, maxY, maxZ).color().setUv(1f, 1f).size(maxZ, maxY)
            .addVertex(model, maxX, minY, maxZ).color().setUv(1f, 0f).size(maxZ, minY)
            .addVertex(model, maxX, minY, minZ).color().setUv(0f, 0f).size(minZ, minY)

        val lineBuffer = multiBufferSource.getBuffer(RenderTypes.lines())

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
            topPercent =
                1 - (((age + widthPart) % total) - widthPart) / (widthPart - (widthPart * widthLine * 1.5f)) + widthLine
            rightPercent = 1 - (age % total - widthPart) / heightPart
            bottomPercent =
                1 - (age % total - widthPart - heightPart) / (widthPart - (widthPart * widthLine * 1.5f)) + widthLine
            leftPercent = 1 - ((age - widthPart) % total - widthPart - heightPart) / heightPart
        } else {
            topPercent = 1 - (((age + widthPart) % total) - widthPart) / widthPart
            rightPercent =
                1 - (((age + widthPart) % total) - widthPart * 2) / (heightPart - (heightPart * heightLine * 1.5f)) + heightLine
            bottomPercent = 1 - (age % total - widthPart - heightPart) / widthPart
            leftPercent =
                1 - ((age - widthPart) % total - widthPart - heightPart) / (heightPart - (heightPart * heightLine * 1.5f)) + heightLine
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

    override fun createRenderState(): TimedoorRenderState = TimedoorRenderState()

    override fun extractRenderState(
        entity: TimedoorEntity,
        state: TimedoorRenderState,
        partialTicks: Float,
    ) {
        super.extractRenderState(entity, state, partialTicks)
        state.openingTime = state.ageInTicks + partialTicks
        state.isUnstable = entity.glitching
        state.placementSettings = entity.sizing
        state.color = entity.color
        state.beganClosing = if (entity.beganClosing == 0) state.ageInTicks.toInt() else entity.beganClosing
        state.closingTime = entity.closingTime
        state.yRot = entity.yRot
    }

    enum class BoxFace {
        FrontBack, TopBottom, LeftRight;

        fun getDimensions(sizing: TimedoorPlacementSettings): Vector2i {
            return when (this) {
                FrontBack -> Vector2i(
                    (sizing.widthAtPercent(1f) * 16).toInt(),
                    (sizing.heightAtPercent(1f) * 16).toInt()
                )

                TopBottom -> Vector2i(
                    (sizing.widthAtPercent(1f) * 16).toInt(),
                    (sizing.depthAtPercent(1f) * 16).toInt()
                )

                LeftRight -> Vector2i(
                    (sizing.depthAtPercent(1f) * 16).toInt(),
                    (sizing.heightAtPercent(1f) * 16).toInt()
                )
            }
        }
    }
}

class TimedoorRenderState : EntityRenderState() {
    var openingTime: Float = 0f
    var isUnstable = false
    var closingTime: Int = 0
    var beganClosing: Int = 0
    var yRot: Float = 0f
    lateinit var placementSettings: TimedoorPlacementSettings
    lateinit var color: Color
}

operator fun Vector2i.component1(): Int = x
operator fun Vector2i.component2(): Int = y
