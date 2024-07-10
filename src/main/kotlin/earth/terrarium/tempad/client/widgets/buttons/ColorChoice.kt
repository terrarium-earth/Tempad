package earth.terrarium.tempad.client.widgets.buttons

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.olympus.client.components.base.ListWidget.Item
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.widgets.HorizontalListWidget
import earth.terrarium.tempad.common.utils.bedrockButton
import earth.terrarium.tempad.common.utils.darken
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

class ColorChoice(val color: Color, val updateColor: (ColorChoice) -> Unit): AbstractButton(0, 0, 14, 14, CommonComponents.EMPTY) {
    companion object {
        const val highlight: Int = 0xa0FFFFFF.toInt()
    }

    var selected = false

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val fillColor = Color(color.intRed, color.intGreen, color.intBlue, 255)
        val dropShadow = Color(color.intRed, color.intGreen, color.intBlue, 128)

        graphics.renderOutline(x + 1, y + 1, width, height, dropShadow.value)

        val x = if(isHovered) x + 1 else x
        val y = if(isHovered) y + 1 else y

        graphics.fill(x, y, x + width, y + height, fillColor.value)

        if (selected || isHovered) {
            graphics.renderOutline(x, y, width, height, highlight)
        }
    }

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        this.defaultButtonNarrationText(pNarrationElementOutput)
    }

    override fun onPress() {
        updateColor(this)
    }

    override fun getRectangle(): ScreenRectangle = ScreenRectangle(x, y, width, height)
}