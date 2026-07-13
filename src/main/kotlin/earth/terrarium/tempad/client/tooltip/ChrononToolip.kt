package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.client.TempadUI
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.neoforged.neoforge.transfer.energy.EnergyHandler

class ChrononTooltip(val container: ChrononData): ClientTooltipComponent {
    override fun getHeight(p0: Font): Int = 14

    override fun getWidth(font: Font): Int = 74

    override fun extractImage(
        font: Font,
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        graphics: GuiGraphicsExtractor,
    ) {
        TempadUI.renderEnergyBar(graphics, font, x, y, container.content, container.capacity)
    }
}

val EnergyHandler.tooltip: TooltipComponent get() = ChrononData(amountAsInt, capacityAsInt)

data class ChrononData(val content: Int, val capacity: Int): TooltipComponent {
    companion object {
        val infinite = ChrononData(-1, -1)
    }
}