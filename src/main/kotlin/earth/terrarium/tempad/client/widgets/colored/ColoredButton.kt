package earth.terrarium.tempad.client.widgets.colored

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.bedrockButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class ColoredButton(
    text: Component,
    val type: ButtonType = ButtonType.SOLID,
    width: Int = Minecraft.getInstance().font.width(text) + 12,
    height: Int = 16,
    x: Int = 0,
    y: Int = 0,
    onPress: (Button) -> Unit,
) :
    Button(x, y, width, height, text, onPress, { e -> e.get() }) {
    var textWidth = Minecraft.getInstance().font.width(text)

    var color = Tempad.ORANGE.value
    var highlightedColor = Tempad.HIGHLIGHTED_ORANGE.value
    var darkColor = Tempad.DARK_ORANGE.value

    override fun renderWidget(graphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val color = if (!isActive) darkColor else if (isHovered) highlightedColor else color

        if (type == ButtonType.FANCY) {
            graphics.bedrockButton(x, y, width, height, isHovered, isActive)
        }

        if (type == ButtonType.OUTLINE) {
            graphics.renderOutline(x + 1, y + 1, width, height, Tempad.DARK_ORANGE.value)

            if (!isHovered && isActive) {
                graphics.renderOutline(x, y, width, height, Tempad.ORANGE.value)
            }
        }

        if (type == ButtonType.SOLID || (type == ButtonType.OUTLINE && isHovered && isActive)) {
            graphics.fill(x, y, x + width, y + height, color)
            graphics.renderOutline(x + 1, y + 1, width - 2, height - 2, 0x1FFFFFAA.toInt())
            graphics.renderOutline(x, y, width, height, 0x3F000000.toInt())
        }


        graphics.drawString(
            Minecraft.getInstance().font,
            this.getMessage(),
            x + (width / 2) - textWidth / 2,
            y + (height / 2) - 4 + if (type == ButtonType.FANCY && isHovered && isActive) 2 else 0,
            when (type) {
                ButtonType.SOLID -> 0xFF000000.toInt()
                ButtonType.FANCY -> 0xFF000000.toInt()
                ButtonType.OUTLINE -> if (isHovered && isActive) 0xFF000000.toInt() else color
                else -> color
            },
            false
        )
    }

    override fun setMessage(pMessage: Component) {
        super.setMessage(pMessage)
        textWidth = Minecraft.getInstance().font.width(pMessage)
    }
}

enum class ButtonType {
    SOLID,
    OUTLINE,
    TEXT,
    FANCY
}