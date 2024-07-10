package earth.terrarium.tempad.client.widgets

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.HistoricalLocation
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

class TimelineEntry(val font: Font, val date: String, val time: String, val location: HistoricalLocation, val onClick: () -> Unit): HorizontalListWidget.Item() {
    override fun getRectangle(): ScreenRectangle = ScreenRectangle(x, y, width, height)

    val parentLeft get() = parent?.let { it.x + 2 } ?: (x + 2)
    val parentRight get() = parent?.let { it.x + it.width - 2 } ?: (x + width - 2)

    val markerComponent: MutableComponent? = location.marker?.let { Component.translatable(it.toLanguageKey("marker")) }
    val locationComponent: MutableComponent = Component.literal("${location.pos.x.toInt()}, ${location.pos.y.toInt()}, ${location.pos.z.toInt()}")
    val dimensionComponent: MutableComponent = Component.translatable(location.dimension.location().toLanguageKey("dimension"))

    val sprite = location.marker?.let { ResourceLocation.fromNamespaceAndPath(it.namespace, "marker/" + it.path) }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, pPartialTick: Float) {
        val hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height

        if (hover) {
            graphics.drawString(font, date, parentRight - font.width(date) - 3, y + 4, Tempad.DARK_ORANGE.value)
            graphics.drawString(font, time, parentRight - font.width(time) - 3, y + 14, Tempad.DARK_ORANGE.value)
            markerComponent?.let { graphics.drawString(font, it, parentLeft + 3, y + 4, Tempad.ORANGE.value) }
            val y = markerComponent?.let { y + 10 } ?: y
            graphics.drawString(font, dimensionComponent, parentLeft + 3, y + 4, Tempad.ORANGE.value)
            graphics.drawString(font, locationComponent, parentLeft + 3, y + 14, Tempad.ORANGE.value)
        }

        sprite?.let { graphics.blitSprite(it, x + 4, y + height - 22, 12, 12) }

        graphics.fill(x + width / 2 - 1, y + height - 6, x + width / 2 + 1, y + height - 2, if(hover) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value)
    }

    override fun isMouseOver(pMouseX: Double, pMouseY: Double): Boolean {
        return pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (isMouseOver(pMouseX, pMouseY)) {
            onClick()
            return true
        }
        return false
    }
}