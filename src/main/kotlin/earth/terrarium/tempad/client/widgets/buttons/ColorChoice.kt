package earth.terrarium.tempad.client.widgets.buttons

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.darken
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class ColorChoice(val color: Color, name: Component, val updateColor: (Color) -> Unit): AbstractButton(0, 0, 14, 14, name) {
    init {
        tooltip = Tooltip.create(name)
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val borderColor: Color
        val fillColor: Color
        if (!active) {
            borderColor = Tempad.DARK_ORANGE
            fillColor = color.darken(0.5f)
        } else if (isHovered) {
            borderColor = Tempad.HIGHLIGHTED_ORANGE
            fillColor = color
        } else {
            borderColor = Tempad.ORANGE
            fillColor = color
        }

        graphics.fill(x, y, x + width, y + height, borderColor.value)
        val finalFillColor = Color(fillColor.intRed, fillColor.intGreen, fillColor.intBlue, 255)
        graphics.fill(x +1, y + 1, x + width - 1, y + height - 1, finalFillColor.value)
    }

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        this.defaultButtonNarrationText(pNarrationElementOutput)
    }

    override fun onPress() {
        updateColor(color)
    }
}