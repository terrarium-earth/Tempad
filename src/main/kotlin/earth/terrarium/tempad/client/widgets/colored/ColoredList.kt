package earth.terrarium.tempad.client.widgets.colored

import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.bedrockButton
import net.minecraft.client.gui.GuiGraphics

open class ColoredList(width: Int, height: Int, spacing: Int = 0): ListWidget(width, height) {
    init {
        this.gap = spacing
    }

    override fun renderScrollbar(
        graphics: GuiGraphics,
        scrollBarX: Int,
        scrollBarY: Int,
        scrollBarHeight: Int,
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float
    ) {
        graphics.fill(
            scrollBarX + 2,
            y,
            scrollBarX + this.scrollbarThumbWidth - 3,
            y + height,
            Tempad.DARK_ORANGE.value
        )

        graphics.bedrockButton(
            scrollBarX,
            scrollBarY,
            this.scrollbarThumbWidth - 1,
            scrollBarHeight,
            false,
            this.isActive
        )
    }



    override fun getScrollbarThumbWidth(): Int = 7
    override fun getScrollbarPadding(): Int = 0

    operator fun iterator(): Iterator<Item> = this.items.iterator()
}