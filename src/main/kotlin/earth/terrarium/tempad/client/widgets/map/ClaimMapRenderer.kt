package earth.terrarium.tempad.client.widgets.map

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.teamresourceful.resourcefullib.client.CloseablePoseStack
import earth.terrarium.tempad.Tempad.Companion.tempadId
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation
import java.util.*

class ClaimMapRenderer(colors: Array<IntArray>, scale: Int) {
    init {
        val textureManager = Minecraft.getInstance().textureManager
        val dynamicTexture = DynamicTexture(scale, scale, true)
        textureManager.register(TEXTURE, dynamicTexture)
        updateTexture(dynamicTexture, colors, scale)
    }

    private fun updateTexture(texture: DynamicTexture, colors: Array<IntArray>, scale: Int) {
        val nativeImage = texture.pixels ?: return
        for (i in 0 until scale) {
            for (j in 0 until scale) {
                nativeImage.setPixelRGBA(i, j, colors[i][j])
            }
        }

        texture.upload()
    }

    fun render(graphics: GuiGraphics?, x: Int, y: Int, size: Int) {
        RenderSystem.setShaderTexture(0, TEXTURE)
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        CloseablePoseStack(graphics).use { pose ->
            pose.translate(x.toDouble(), y.toDouble(), 1.0)
            val matrix4f = pose.last().pose()
            val builder =
                Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
            builder.addVertex(matrix4f, 0.0f, size.toFloat(), -0.01f)
                .setUv(0.0f, 1.0f)
                .addVertex(matrix4f, size.toFloat(), size.toFloat(), -0.01f)
                .setUv(1.0f, 1.0f)
                .addVertex(matrix4f, size.toFloat(), 0.0f, -0.01f)
                .setUv(1.0f, 0.0f)
                .addVertex(matrix4f, 0.0f, 0.0f, -0.01f)
                .setUv(0.0f, 0.0f)
            builder.build()?.let { BufferUploader.drawWithShader(it) }
        }
    }

    companion object {
        private val TEXTURE = "claimmaptextures".tempadId
    }
}