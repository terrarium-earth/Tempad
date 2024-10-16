package earth.terrarium.tempad.client.tooltip

import com.teamresourceful.resourcefullibkt.client.scissor
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.tooltip.TooltipComponent

class ChrononTooltip(container: ChrononData): ClientTooltipComponent {
    val text = if(container.capacity == -1) Component.translatable("item.tempad.sacred_chronometer.infinite") else Component.literal("${container.content} / ${container.capacity}")

    val percentage = if(container.capacity == -1) 1.0 else container.content.toDouble() / container.capacity.toDouble()

    val contentHeight = 16

    override fun getHeight(): Int = contentHeight + 4
    override fun getWidth(font: Font): Int = (font.width(text) + 8).coerceAtLeast(56)

    fun GuiGraphics.drawCenteredText(font: Font, x: Int, y: Int, color: Int) {
        this.drawString(font, text, x - font.width(text.visualOrderText) / 2, y, color, false)
    }

    override fun renderImage(font: Font, x: Int, y: Int, graphics: GuiGraphics) {
        val width = getWidth(font)
        graphics.fill(x, y, x + width, y + contentHeight, Tempad.ORANGE.value)
        val barWidth = (width * percentage).toInt()
        graphics.fill(x + barWidth, y, x + width, y + contentHeight, Tempad.DARK_ORANGE.value)
        graphics.renderOutline(x, y, width, contentHeight, 0x66000000)
        graphics.renderOutline(x + 1, y + 1, width - 2, contentHeight - 2, 0x33FFFFFF)

        graphics.scissor(x, y, barWidth, contentHeight) {
            graphics.drawCenteredText(font, x + width / 2, y + 4, 0xFF000000.toInt())
        }

        graphics.scissor(x + barWidth, y, width - barWidth, contentHeight) {
            graphics.drawCenteredText(font, x + width / 2, y + 4, Tempad.ORANGE.value)
        }
    }
}

val ChrononHandler.tooltip: TooltipComponent get() = ChrononData(power, maxPower)

data class ChrononData(val content: Int, val capacity: Int): TooltipComponent {
    companion object {
        val infinite = ChrononData(-1, -1)
    }
}