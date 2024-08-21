package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.api.locations.PlayerPos
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import org.joml.Matrix4f

class PlayerPosTooltip(val pos: PlayerPos): ClientTooltipComponent {
    val text = Component.literal(pos.playerProfile.name)

    val playerTexture get() = Minecraft.getInstance().skinManager.getInsecureSkin(pos.playerProfile).texture()

    override fun getHeight(): Int = 10
    override fun getWidth(font: Font): Int = font.width(text) + 11

    override fun renderText(
        font: Font,
        mouseX: Int,
        mouseY: Int,
        matrix: Matrix4f,
        bufferSource: MultiBufferSource.BufferSource,
    ) {
        super.renderText(font, mouseX, mouseY, matrix, bufferSource)
        font.drawInBatch(
            text,
            mouseX.toFloat() + 11,
            mouseY.toFloat() + 1,
            text.style.color?.value ?: -1,
            true,
            matrix,
            bufferSource,
            Font.DisplayMode.NORMAL,
            0,
            15728880
        )
    }

    override fun renderImage(font: Font, x: Int, y: Int, guiGraphics: GuiGraphics) {
        super.renderImage(font, x, y, guiGraphics)
        PlayerFaceRenderer.draw(guiGraphics, playerTexture, x, y, 9)
    }
}