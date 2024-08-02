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
            scrollBarX + this.scrollbarPadding,
            y + this.scrollbarPadding,
            scrollBarX + this.scrollbarPadding + this.scrollbarThumbWidth - this.scrollbarPadding * 2,
            y + height - this.scrollbarPadding,
            Tempad.DARK_ORANGE.value
        )

        graphics.bedrockButton(
            scrollBarX,
            scrollBarY,
            this.scrollbarThumbWidth,
            scrollBarHeight,
            false,
            this.isActive
        )
    }

    override fun getScrollbarThumbWidth(): Int = 6
    override fun getScrollbarPadding(): Int = 2

    operator fun iterator(): Iterator<Item> = this.items.iterator()
}