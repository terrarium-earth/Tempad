package earth.terrarium.tempad.client.widgets

import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.LocationData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.Mth

class InformationPanel : ListWidget(92, 76) {
    companion object {
        private val EMPTY_LINE_1 = StringEntry(Component.translatable(("gui.${Tempad.MOD_ID}.no_selection.first_line")))
        private val EMPTY_LINE_2 = StringEntry(Component.translatable(("gui.${Tempad.MOD_ID}.no_selection.second_line")))
        private val NO_SELECTION = listOf(EMPTY_LINE_1, EMPTY_LINE_2)

        private val LOCATION_X = "gui.${Tempad.MOD_ID}.x"
        private val LOCATION_Y = "gui.${Tempad.MOD_ID}.y"
        private val LOCATION_Z = "gui.${Tempad.MOD_ID}.z"
        private val ANGLE = "gui.${Tempad.MOD_ID}.angle"
        private val LOCATION_DIMENSION = "gui.${Tempad.MOD_ID}.dimension"

        fun getEntries(pos: GlobalPos): List<MutableComponent> {
            return listOf(
                Component.translatable(LOCATION_X, pos.pos().x),
                Component.translatable(LOCATION_Y, pos.pos().y),
                Component.translatable(LOCATION_Z, pos.pos().z),
            )
        }
    }

    init {
        gap = 2
        set(NO_SELECTION)
    }

    fun update(location: LocationData) {
        set(listOf(
            StringEntry(Component.literal(location.name)),
            StringEntry(Component.translatable(LOCATION_X, Mth.floor(location.x))),
            StringEntry(Component.translatable(LOCATION_Y, Mth.floor(location.y))),
            StringEntry(Component.translatable(LOCATION_Z, Mth.floor(location.z))),
            StringEntry(Component.translatable(ANGLE, location.angle)),
            StringEntry(Component.translatable(LOCATION_DIMENSION, location.dimComponent))
        ))
    }

    fun clearLines() {
        set(NO_SELECTION)
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
}
