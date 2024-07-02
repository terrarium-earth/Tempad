package earth.terrarium.tempad.common.utils

import earth.terrarium.tempad.Tempad
import net.minecraft.client.gui.GuiGraphics

fun GuiGraphics.bedrockButton(x: Int, y: Int, width: Int, height: Int, hovered: Boolean, active: Boolean) {
    fill(
        x,
        y,
        x + width,
        y + height,
        if (!active) Tempad.DARK_ORANGE.value else if (hovered) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value
    )
    fill(x + 1, y + height - 3, x + width - 1, y + height - 1, 0x66000000)

    if (width > 12) {
        renderOutline(x + 1, y + 1, width - 2, height - 4, 0x33FFFFFF)
    } else {
        hLine(x + 1, x + width - 1, y + 1, 0x33FFFFFF)
    }

    renderOutline(x, y, width, height, 0xaa000000.toInt())
}