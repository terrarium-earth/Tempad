package earth.terrarium.tempad.client.widgets

import com.ibm.icu.text.DateFormat
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.HistoricalLocation
import earth.terrarium.tempad.common.utils.component
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import java.util.*

class TimelineEntry(parentWidth: Int, val font: Font, val date: Date, val location: HistoricalLocation): HorizontalListWidget.Item() {
    companion object {
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Minecraft.getInstance().locale)
    }

    override fun getRectangle(): ScreenRectangle = ScreenRectangle(x, y, width, height)

    val dateDisplay = Component.literal(dateFormat.format(date))

    val parentCenter get() = parent?.let { it.x + it.width / 2 } ?: (x + width / 2)

    val markerComponent: MutableComponent = location.marker?.let { Component.translatable(it.toLanguageKey("marker")) } ?: Component.translatable("misc.tempad.wandering")
    val locationComponent: Component = location.pos.component
    val dimensionComponent: Component = Component.translatable(location.dimension.location().toLanguageKey("dimension"))

    val text: List<FormattedCharSequence> = font.split(markerComponent.append(" ").append(dimensionComponent), parentWidth - 6) + locationComponent.visualOrderText + dateDisplay.visualOrderText

    val sprite = location.marker?.let { ResourceLocation.fromNamespaceAndPath(it.namespace, "marker/" + it.path) }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, pPartialTick: Float) {
        if (current) {
            for ((index, line) in text.withIndex()) {
                val isLast = index == text.size - 1
                graphics.drawCenteredString(font, line, parentCenter, y + 4 + index * 10, if (isLast) Tempad.DARK_ORANGE.value else Tempad.ORANGE.value)
            }
        }

        sprite?.let { graphics.blitSprite(it, x + 4, y + height - 22, 12, 12) }

        graphics.fill(x + width / 2 - 1, y + height - 6, x + width / 2 + 1, y + height - 2, if(current) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value)
    }
}