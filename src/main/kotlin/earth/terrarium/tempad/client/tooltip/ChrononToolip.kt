package earth.terrarium.tempad.client.tooltip

import com.teamresourceful.resourcefullibkt.client.scissor
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.items.ChrononContainer
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.tooltip.TooltipComponent

class ChrononTooltip(container: ChrononData): ClientTooltipComponent {
    val text = Component.literal("${container.content} / ${container.capacity}")

    val percentage = container.content.toDouble() / container.capacity.toDouble()

    override fun getHeight(): Int = 16
    override fun getWidth(font: Font): Int = (font.width(text) + 8).coerceAtLeast(56)

    fun GuiGraphics.drawCenteredText(font: Font, x: Int, y: Int, color: Int) {
        this.drawString(font, text, x - font.width(text.visualOrderText) / 2, y, color, false)
    }

    override fun renderImage(font: Font, x: Int, y: Int, graphics: GuiGraphics) {
        val width = getWidth(font)
        graphics.fill(x, y, x + width, y + height, Tempad.ORANGE.value)
        val barWidth = (width * percentage).toInt()
        graphics.fill(x + barWidth, y, x + width, y + height, Tempad.DARK_ORANGE.value)
        graphics.renderOutline(x, y, width, height, 0x66000000)
        graphics.renderOutline(x + 1, y + 1, width - 2, height - 2, 0x33FFFFFF)

        graphics.scissor(x, y, barWidth, height) {
            graphics.drawCenteredText(font, x + width / 2, y + 4, 0xFF000000.toInt())
        }

        graphics.scissor(x + barWidth, y, width - barWidth, height) {
            graphics.drawCenteredText(font, x + width / 2, y + 4, Tempad.ORANGE.value)
        }
    }
}

val ChrononContainer.tooltip: TooltipComponent get() = ChrononData(content, capacity)

data class ChrononData(val content: Int, val capacity: Int): TooltipComponent