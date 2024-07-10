package earth.terrarium.tempad.client.widgets

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.widgets.location_panel.KListWidgetItem
import earth.terrarium.tempad.common.data.HistoricalLocation
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

class TimelineEntry(val font: Font, val location: HistoricalLocation, val onClick: () -> Unit): KListWidgetItem, HorizontalListWidget.Item {
    override var _x: Int = 0
    override var _y: Int = 0
    override var _focused: Boolean = false

    override fun getRectangle(): ScreenRectangle = ScreenRectangle(x, y, width, height)

    override var parent: HorizontalListWidget? = null

    override var _width: Int = 20
    override var _height: Int = parent?.height ?: 20

    val textCenter get() = parent?.let { it.x + it.width / 2 } ?: (x + width / 2)
    val textLeft get() = parent?.let { it.x + 2 } ?: (x + 2)
    val textRight get() = parent?.let { it.x + it.width - 2 } ?: (x + width - 2)

    val markerComponent: MutableComponent? = location.marker?.let { Component.translatable(it.toLanguageKey("marker")) }
    val locationComponent: MutableComponent = Component.literal("${location.pos.x.toInt()}, ${location.pos.y.toInt()}, ${location.pos.z.toInt()}")
    val dimensionComponent: MutableComponent = Component.translatable(location.dimension.location().toLanguageKey("dimension"))

    val sprite = location.marker?.let { ResourceLocation.fromNamespaceAndPath(it.namespace, "marker/" + it.path) }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, pPartialTick: Float) {
        val hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height

        if (hover) {
            markerComponent?.let { graphics.drawCenteredString(font, it, textCenter, y + 6, Tempad.ORANGE.value) }

            if (font.width(locationComponent) > (parent?.width ?: width)) {
                graphics.drawScrollingString(font, locationComponent, textLeft, textRight, y + 17, Tempad.ORANGE.value)
            } else {
                graphics.drawCenteredString(font, locationComponent, textCenter, y + 17, Tempad.ORANGE.value)
            }

            if (font.width(dimensionComponent) > (parent?.width ?: width)) {
                graphics.drawScrollingString(font, dimensionComponent, textLeft, textRight, y + 28, Tempad.ORANGE.value)
            } else {
                graphics.drawCenteredString(font, dimensionComponent, textCenter, y + 28, Tempad.ORANGE.value)
            }
        }

        sprite?.let { graphics.blitSprite(it, x + 4, y + 40, 12, 12) }

        graphics.fill(x + width / 2 - 1, y + height - 25, x + width / 2 + 1, y + height - 25, if(hover) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value)
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