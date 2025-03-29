package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.client.TempadUI
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.inventory.tooltip.TooltipComponent

class ChrononTooltip(val container: ChrononData): ClientTooltipComponent {
    override fun getHeight(): Int = 17

    override fun getWidth(font: Font): Int = 74

    override fun renderImage(font: Font, x: Int, y: Int, graphics: GuiGraphics) {
        TempadUI.renderEnergyBar(graphics, font, x, y, container.content, container.capacity)
    }
}

val ChrononHandler.tooltip: TooltipComponent get() = ChrononData(power, maxPower)

data class ChrononData(val content: Int, val capacity: Int): TooltipComponent {
    companion object {
        val infinite = ChrononData(-1, -1)
    }
}