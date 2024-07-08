package earth.terrarium.tempad.client.widgets.colored

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.bedrockButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class ColoredButton(text: Component, val type: ButtonType = ButtonType.OUTLINE, width: Int = Minecraft.getInstance().font.width(text) + 12, height: Int = 14, x: Int = 0, y: Int = 0, onPress: (Button) -> Unit) :
    Button(x, y, width, height, text, onPress, { e -> e.get() }) {
    val textWidth = Minecraft.getInstance().font.width(text)

    var color = Tempad.ORANGE.value
    var highlightedColor = Tempad.HIGHLIGHTED_ORANGE.value
    var darkColor = Tempad.DARK_ORANGE.value

    override fun renderWidget(graphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val color = if(!isActive) darkColor else if (isHovered) highlightedColor else color

        if (type == ButtonType.FANCY) {
            graphics.bedrockButton(x, y, width, height, isHovered, isActive)
        }

        if (type == ButtonType.SOLID || (type == ButtonType.OUTLINE && isHovered)) {
            graphics.fill(x, y, x + width, y + height, Tempad.DARK_ORANGE.value)
        }

        if (type == ButtonType.OUTLINE && !isHovered) {
            graphics.renderOutline(x + 1, y + 1, x + width - 1, y + height - 1, Tempad.ORANGE.value)
        }

        graphics.drawString(
            Minecraft.getInstance().font,
            this.getMessage(),
            x + (width / 2) - textWidth / 2,
            y + (height / 2) - 4 + if(isHovered && isActive) 2 else 0,
            when (type) {
                ButtonType.SOLID -> 0xFF000000.toInt()
                ButtonType.FANCY -> 0xFF000000.toInt()
                ButtonType.OUTLINE -> if(isHovered && isActive) 0xFF000000.toInt() else color
                else -> color
            },
            false
        )
    }
}

enum class ButtonType {
    SOLID,
    OUTLINE,
    TEXT,
    FANCY
}