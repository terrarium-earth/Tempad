package earth.terrarium.tempad.client.widgets.buttons

import earth.terrarium.tempad.Tempad
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class FlatButton(text: Component, val type: ButtonType = ButtonType.SOLID, x: Int = 0, y: Int = 0, onPress: (Button) -> Unit) :
    Button(Minecraft.getInstance().font.width(text) + 12, 14, x, y, text, onPress, { e -> e.get() }) {

    override fun renderWidget(graphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val color = if(!isActive) Tempad.DARK_ORANGE.value else if (isHovered) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value

        if (type == ButtonType.SOLID || (type == ButtonType.OUTLINE && isHovered)) {
            graphics.fill(x, y, x + width, y + height, Tempad.DARK_ORANGE.value)
        }

        if (type == ButtonType.OUTLINE && !isHovered) {
            graphics.renderOutline(x + 1, y + 1, x + width - 1, y + height - 1, Tempad.ORANGE.value)
        }

        graphics.drawString(
            Minecraft.getInstance().font,
            this.getMessage(),
            x + 6,
            y + 4,
            when (type) {
                ButtonType.SOLID -> 0xFF000000.toInt()
                ButtonType.OUTLINE -> if(isHovered && isActive) 0xFF000000.toInt() else color
                else -> color
            }
        )
    }
}

enum class ButtonType {
    SOLID,
    OUTLINE,
    TEXT
}