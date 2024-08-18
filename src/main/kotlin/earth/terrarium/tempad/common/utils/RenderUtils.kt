package earth.terrarium.tempad.common.utils

import com.teamresourceful.resourcefullib.client.utils.RenderUtils
import earth.terrarium.tempad.Tempad
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

fun GuiGraphics.bedrockButton(x: Int, initY: Int, width: Int, initHeight: Int, hovered: Boolean, active: Boolean, focused: Boolean = false, color: Int = Tempad.ORANGE.value) {
    val height = initHeight - if (hovered && active) 2 else 0
    val y = initY + if (hovered && active) 2 else 0

    fill(
        x,
        y,
        x + width,
        y + height,
        color
    )

    if (!hovered || !active) {
        fill(x + 1, y + height - 3, x + width - 1, y + height - 1, 0x66000000)
    }

    if (width > 12) {
        renderOutline(x + 1, y + 1, width - 2, height - if(hovered && active) 2 else 4, 0x33FFFFFF)
    } else {
        hLine(x + 1, x + width - 1, y + 1, 0x33FFFFFF)
    }

    renderOutline(x, y, width, height, if(focused) 0xaaFFFFFF.toInt() else 0x99000000.toInt())

    if (!active) {
        fill(
            x,
            y,
            x + width,
            y + height,
            0x66000000
        )
    }
}