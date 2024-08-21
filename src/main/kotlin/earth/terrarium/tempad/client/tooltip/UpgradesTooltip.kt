package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.InstalledUpgradesComponent
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f

class UpgradesTooltip(val upgrades: InstalledUpgradesComponent): ClientTooltipComponent {
    val text = upgrades.upgrades.map { Component.translatable(it.toLanguageKey("upgrade")) }
    val images = upgrades.upgrades.map { ResourceLocation.fromNamespaceAndPath(it.namespace, "upgrade/" + it.path) }

    override fun getHeight(): Int = upgrades.upgrades.size * 12 + 14

    override fun getWidth(font: Font): Int = text.maxOf { font.width(it) } + 13

    override fun renderText(
        font: Font,
        mouseX: Int,
        mouseY: Int,
        matrix: Matrix4f,
        bufferSource: MultiBufferSource.BufferSource,
    ) {
        font.drawInBatch(
            Component.translatable("misc.tempad.upgrades"),
            mouseX.toFloat(),
            mouseY.toFloat(),
            -1,
            true,
            matrix,
            bufferSource,
            Font.DisplayMode.NORMAL,
            0,
            15728880
        )

        for ((index, line) in text.withIndex()) {
            font.drawInBatch(
                line,
                mouseX.toFloat() + 13,
                mouseY.toFloat() + 1 + (index + 1) * 12f,
                ChatFormatting.GRAY.color ?: -1,
                true,
                matrix,
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                15728880
            )
        }
    }

    override fun renderImage(font: Font, x: Int, y: Int, guiGraphics: GuiGraphics) {
        super.renderImage(font, x, y, guiGraphics)
        for ((index, image) in images.withIndex()) {
            guiGraphics.blitSprite(image, x, y + (index + 1) * 12, 9, 9)
        }
    }
}