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
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import org.joml.Vector2i

class TimedoorRenderer(val ctx: EntityRendererProvider.Context) :
    EntityRenderer<TimedoorEntity, TimedoorRenderState>(ctx) {
    companion object {
        private val faceTextures = hashMapOf<Pair<TimedoorPlacementSettings, BoxFace>, Identifier>()
    }

    override fun submit(
        entity: TimedoorRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState,
    ) {
        if (entity.ageInTicks < TimedoorEntity.IDLE_BEFORE_START) return
        val ticks = entity.partialTick + entity.ageInTicks - TimedoorEntity.IDLE_BEFORE_START
        val tickLength = TimedoorEntity.ANIMATION_LENGTH
        val animation: Float

        if (entity.ageInTicks > entity.maxLifeTime) {
            animation = Mth.clamp(1 - (ticks - TimedoorEntity.ANIMATION_LENGTH - entity.maxLifeTime) / tickLength.toFloat(), 0f, 1f)
        } else {
            animation = Mth.clamp((ticks) / tickLength.toFloat(), 0f, 1f)
        }

        var width = entity.placementSettings.widthAtPercent(animation)
        var height = entity.placementSettings.heightAtPercent(animation)
        var depth = entity.placementSettings.depthAtPercent(animation)
        val finalHeight = entity.placementSettings.dimensions.height
        var color = entity.color.withAlpha(255)

        poseStack.pushPose()
        poseStack.mulPose(Axis.YN.rotationDegrees(entity.yRot + 180))
        if (entity.nextGlitchTime > 0 && (ticks.toInt() in entity.nextGlitchTime .. entity.nextGlitchTime + 15)) {
            val instability = entity.instability / 250f
            val randomX = Mth.randomBetween(clientLevel?.random!!, -instability, instability)
            val randomY = Mth.randomBetween(clientLevel?.random!!, -instability, instability)
            val randomZ = Mth.randomBetween(clientLevel?.random!!, -instability, instability)
            width *= (1 + Mth.randomBetween(clientLevel?.random!!, -instability, instability))
            height *= (1 + Mth.randomBetween(clientLevel?.random!!, -instability, instability))
            depth *= (1 + Mth.randomBetween(clientLevel?.random!!, -instability, instability))
            color = color.withAlpha(Mth.nextInt(clientLevel?.random!!, 0, 255))

            poseStack.translate(randomX, randomY, randomZ)
        }
        poseStack.translate(width / -2.0, finalHeight / 2.0 - height / 2.0 + 0.01, depth / -2.0)

        if (width > 0) renderTimedoor(
            entity.placementSettings,
            poseStack,
            submitNodeCollector,
            width,
            height,
            depth,
            color,
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
        if (!ShaderModBridge.irisEnabled) return null
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
        submitNodeCollector: SubmitNodeCollector,
        width: Float,
        height: Float,
        depth: Float,
        color: Color,
        age: Float,
        animate: Boolean = true,
    ) {
        val maxX = width
        val maxY = height
        val maxZ = depth
        val minX = 0f
        val minY = 0f
        val minZ = 0f

        //Front
        val red = color.floatRed
        val green = color.floatGreen
        val blue = color.floatBlue
        val alpha = color.floatAlpha

        fun VertexConsumer.color() = setColor(red, green, blue, alpha).setLineWidth(2f)
        fun VertexConsumer.white() = setColor(1f, 1f, 1f, 1f).setLineWidth(2f)

        fun VertexConsumer.size(u: Float, v: Float): VertexConsumer {
            setLineWidth(2f)
            if (!ShaderModBridge.irisEnabled) {
                setUv2((u * 16).toInt(), (v * 16).toInt())
            }
            return this
        }

        submitNodeCollector.submitCustomGeometry(
            poseStack,
            TempadClient.renderType(registerFaceTexture(sizing, BoxFace.FrontBack))
        ) { model, buffer ->
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
        }

        submitNodeCollector.submitCustomGeometry(
            poseStack,
            TempadClient.renderType(registerFaceTexture(sizing, BoxFace.TopBottom))
        ) { model, buffer ->
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
        }


        submitNodeCollector.submitCustomGeometry(
            poseStack,
            TempadClient.renderType(registerFaceTexture(sizing, BoxFace.LeftRight))
        ) { model, buffer ->
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
        }

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines()) { model, lineBuffer ->
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
            lineBuffer.addVertex(model, minX, maxY, minZ).color().setNormal(model, -1.0F, 0.0F, 0.0F)
            if (animate && topPercent > -widthLine && topPercent < (1 + widthLine)) {
                val start = Mth.clamp(topPercent - widthLine, 0f, 1f)
                val end = Mth.clamp(topPercent + widthLine, 0f, 1f)
                val middle = Mth.clamp(topPercent, 0f, 1f)

                lineBuffer.addVertex(model, Mth.lerp(start, 0f, maxX), maxY, minZ)
                    .color()
                    .setNormal(model, -1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(start, 0f, maxX), maxY, minZ)
                    .color()
                    .setNormal(model, -1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(middle, 0f, maxX), maxY, minZ)
                    .white()
                    .setNormal(model, -1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(middle, 0f, maxX), maxY, minZ)
                    .white()
                    .setNormal(model, -1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(end, 0f, maxX), maxY, minZ)
                    .color()
                    .setNormal(model, -1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(end, 0f, maxX), maxY, minZ)
                    .color()
                    .setNormal(model, -1.0F, 0.0F, 0.0F)
            }
            lineBuffer.addVertex(model, maxX, maxY, minZ).color().setNormal(model, -1.0F, 0.0F, 0.0F)


            lineBuffer.addVertex(model, minX, minY, minZ).color().setNormal(model, 0.0F, 1.0F, 0.0F)
            if (animate && rightPercent > -heightLine && rightPercent < (1 + heightLine)) {
                val start = Mth.clamp(rightPercent - heightLine, 0f, 1f)
                val end = Mth.clamp(rightPercent + heightLine, 0f, 1f)
                val middle = Mth.clamp(rightPercent, 0f, 1f)

                lineBuffer.addVertex(model, minX, Mth.lerp(start, 0f, maxY), minZ)
                    .color()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, minX, Mth.lerp(start, 0f, maxY), minZ)
                    .color()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, minX, Mth.lerp(middle, 0f, maxY), minZ)
                    .white()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, minX, Mth.lerp(middle, 0f, maxY), minZ)
                    .white()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, minX, Mth.lerp(end, 0f, maxY), minZ)
                    .color()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, minX, Mth.lerp(end, 0f, maxY), minZ)
                    .color()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
            }
            lineBuffer.addVertex(model, minX, maxY, minZ).color().setNormal(model, 0.0F, 1.0F, 0.0F)


            lineBuffer.addVertex(model, minX, minY, minZ).color().setNormal(model, 1.0F, 0.0F, 0.0F);
            if (animate && bottomPercent > -widthLine && bottomPercent < (1 + widthLine)) {
                val start = Mth.clamp(bottomPercent + widthLine, 0f, 1f)
                val end = Mth.clamp(bottomPercent - widthLine, 0f, 1f)
                val middle = Mth.clamp(bottomPercent, 0f, 1f)

                lineBuffer.addVertex(model, Mth.lerp(1 - start, 0f, maxX), minY, minZ)
                    .color()
                    .setNormal(model, 1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(1 - start, 0f, maxX), minY, minZ)
                    .color()
                    .setNormal(model, 1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(1 - middle, 0f, maxX), minY, minZ)
                    .white()
                    .setNormal(model, 1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(1 - middle, 0f, maxX), minY, minZ)
                    .white()
                    .setNormal(model, 1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(1 - end, 0f, maxX), minY, minZ)
                    .color()
                    .setNormal(model, 1.0F, 0.0F, 0.0F)
                lineBuffer.addVertex(model, Mth.lerp(1 - end, 0f, maxX), minY, minZ)
                    .color()
                    .setNormal(model, 1.0F, 0.0F, 0.0F)
            }
            lineBuffer.addVertex(model, maxX, minY, minZ).color().setNormal(model, 1.0F, 0.0F, 0.0F)


            lineBuffer.addVertex(model, maxX, maxY, minZ).color().setNormal(model, 0.0F, 1.0F, 0.0F)
            if (animate && leftPercent > -heightLine && leftPercent < (1 + heightLine)) {
                val start = Mth.clamp(leftPercent - heightLine, 0f, 1f)
                val end = Mth.clamp(leftPercent + heightLine, 0f, 1f)
                val middle = Mth.clamp(leftPercent, 0f, 1f)

                lineBuffer.addVertex(model, maxX, Mth.lerp(start, maxY, 0f), minZ)
                    .color()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, maxX, Mth.lerp(start, maxY, 0f), minZ)
                    .color()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, maxX, Mth.lerp(middle, maxY, 0f), minZ)
                    .white()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, maxX, Mth.lerp(middle, maxY, 0f), minZ)
                    .white()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, maxX, Mth.lerp(end, maxY, 0f), minZ)
                    .color()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
                lineBuffer.addVertex(model, maxX, Mth.lerp(end, maxY, 0f), minZ)
                    .color()
                    .setNormal(model, 0.0F, 1.0F, 0.0F)
            }
            lineBuffer.addVertex(model, maxX, minY, minZ).color().setNormal(model, 0.0F, 1.0F, 0.0F)
                // connecting front and back
                .addVertex(model, minX, minY, minZ).color().setNormal(model, 0.0F, 0.0F, 1.0F)
                .addVertex(model, minX, minY, maxZ).color().setNormal(model, 0.0F, 0.0F, 1.0F)

                .addVertex(model, maxX, minY, minZ).color().setNormal(model, 0.0F, 0.0F, -1.0F)
                .addVertex(model, maxX, minY, maxZ).color().setNormal(model, 0.0F, 0.0F, -1.0F)

                .addVertex(model, minX, maxY, minZ).color().setNormal(model, 0.0F, 0.0F, 1.0F)
                .addVertex(model, minX, maxY, maxZ).color().setNormal(model, 0.0F, 0.0F, 1.0F)

                .addVertex(model, maxX, maxY, minZ).color().setNormal(model, 0.0F, 0.0F, 1.0F)
                .addVertex(model, maxX, maxY, maxZ).color().setNormal(model, 0.0F, 0.0F, 1.0F)

                //back
                .addVertex(model, minX, maxY, maxZ).color().setNormal(model, 0.0F, -1.0F, 0.0F)
                .addVertex(model, minX, minY, maxZ).color().setNormal(model, 0.0F, -1.0F, 0.0F)
                .addVertex(model, minX, minY, maxZ).color().setNormal(model, 1.0F, 0.0F, 0.0F)
                .addVertex(model, maxX, minY, maxZ).color().setNormal(model, 1.0F, 0.0F, 0.0F)
                .addVertex(model, maxX, minY, maxZ).color().setNormal(model, 0.0F, 1.0F, 0.0F)
                .addVertex(model, maxX, maxY, maxZ).color().setNormal(model, 0.0F, 1.0F, 0.0F)
                .addVertex(model, maxX, maxY, maxZ).color().setNormal(model, 1.0F, 0.0F, 0.0F)
                .addVertex(model, minX, maxY, maxZ).color().setNormal(model, 1.0F, 0.0F, 0.0F)
        }
    }

    override fun createRenderState(): TimedoorRenderState = TimedoorRenderState()

    override fun extractRenderState(
        entity: TimedoorEntity,
        state: TimedoorRenderState,
        partialTicks: Float,
    ) {
        super.extractRenderState(entity, state, partialTicks)
        state.instability = entity.instability
        state.nextGlitchTime = entity.currentGlitchTime
        state.placementSettings = entity.sizing
        state.color = entity.color
        state.maxLifeTime = entity.maxLifeTime
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
    var instability = 0
    var maxLifeTime: Int = 0
    var yRot: Float = 0f
    var nextGlitchTime: Int = 0
    lateinit var placementSettings: TimedoorPlacementSettings
    lateinit var color: Color
}

operator fun Vector2i.component1(): Int = x
operator fun Vector2i.component2(): Int = y
