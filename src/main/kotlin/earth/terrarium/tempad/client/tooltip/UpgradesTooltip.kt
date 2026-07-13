package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.common.data.InstalledUpgradesComponent
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

class UpgradesTooltip(val upgrades: InstalledUpgradesComponent): ClientTooltipComponent {
    val text = upgrades.upgrades.map { Component.translatable(it.toLanguageKey("upgrade")) }
    val images = upgrades.upgrades.map { Identifier.fromNamespaceAndPath(it.namespace, "upgrade/" + it.path) }

    override fun getHeight(p0: Font): Int = upgrades.upgrades.size * 12 + 14

    override fun getWidth(font: Font): Int = text.maxOf { font.width(it) } + 13

    override fun extractText(graphics: GuiGraphicsExtractor, font: Font, x: Int, y: Int) {
        super.extractText(graphics, font, x, y)
        graphics.text(
            font,
            Component.translatable("misc.tempad.upgrades"),
            x + 11,
            y + 1,
            -1
        )

        for ((index, line) in text.withIndex()) {
            graphics.text(
                font,
                line,
                x + 11,
                y + 1 + (index + 1) * 12,
                ChatFormatting.GRAY.color ?: -1,
            )
        }
    }

    override fun extractImage(font: Font, x: Int, y: Int, w: Int, h: Int, graphics: GuiGraphicsExtractor) {
        super.extractImage(font, x, y, w, h, graphics)
        for ((index, image) in images.withIndex()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, image, x, y + 1 + (index + 1) * 12, 8, 7)
        }
    }
}