package earth.terrarium.tempad.client.widgets

import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.tempad.Tempad
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component

class InformationPanel(font: Font, width: Int, height: Int) : ListWidget(width, height) {
    private val NO_SELECTION: MultilineEntry = MultilineEntry(Component.translatable(("app.${Tempad.MOD_ID}.teleport.no_selection")), font).apply {
        setWidth(width)
        setMaxWidth(width - 8)
    }

    init {
        gap = 2
        set(listOf(NO_SELECTION))
    }

    fun update(display: List<Component>) {
        set(display.map { StringEntry(it) })
    }

    fun clearLines() {
        set(listOf(NO_SELECTION))
    }
}

class StringEntry(
    text: Component,
) : StringWidget(text, Minecraft.getInstance().font), ListWidget.Item {
    override fun getRectangle(): ScreenRectangle {
        return super<StringWidget>.getRectangle()
    }

    init {
        setColor(Tempad.ORANGE.value)
        alignLeft()
    }

    override fun getX(): Int = super.getX() + 4
    override fun getY(): Int = super.getY() + 4
    override fun getWidth(): Int = super<StringWidget>.width - 4
}

class MultilineEntry(text: Component, font: Font) : MultiLineTextWidget(text, font), ListWidget.Item {
    override fun getRectangle(): ScreenRectangle {
        return super<MultiLineTextWidget>.getRectangle()
    }

    init {
        setColor(Tempad.ORANGE.value)
    }

    override fun getX(): Int = super.getX() + 4
    override fun getY(): Int = super.getY() + 4
}
