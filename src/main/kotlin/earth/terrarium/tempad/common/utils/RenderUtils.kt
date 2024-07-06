package earth.terrarium.tempad.common.utils

import earth.terrarium.tempad.Tempad
import net.minecraft.client.gui.GuiGraphics

fun GuiGraphics.bedrockButton(x: Int, initY: Int, width: Int, initHeight: Int, hovered: Boolean, active: Boolean) {
    val height = initHeight - if (hovered && active) 2 else 0
    val y = initY + if (hovered && active) 2 else 0

    fill(
        x,
        y,
        x + width,
        y + height,
        if (active) Tempad.ORANGE.value else Tempad.DARK_ORANGE.value
    )

    if (!hovered) {
        fill(x + 1, y + height - 3, x + width - 1, y + height - 1, 0x66000000)
    }

    if (width > 12) {
        renderOutline(x + 1, y + 1, width - 2, height - if(hovered && active) 2 else 4, 0x33FFFFFF)
    } else {
        hLine(x + 1, x + width - 1, y + 1, 0x33FFFFFF)
    }

    if (hovered && active) {
        renderOutline(x, y, width, height, 0xaaFFFFFF.toInt())
    } else {
        renderOutline(x, y, width, height, 0x66000000)
    }
}