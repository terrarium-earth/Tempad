package earth.terrarium.tempad.client.widgets

import com.teamresourceful.resourcefullib.client.components.selection.SelectionList
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.LocationData
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import java.util.function.Consumer

class InformationPanel @JvmOverloads constructor(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val color: Int = Tempad.ORANGE.value,
    private val shadow: Boolean = true
) :
    SelectionList<TextEntry?>(x, y, width, height, 10, Consumer { _ -> }) {
    companion object {
        private val EMPTY_LINE_1 = TextEntry(Component.translatable(("gui.${Tempad.MOD_ID}.no_selection.first_line")))
        private val EMPTY_LINE_2 = TextEntry(Component.translatable(("gui.${Tempad.MOD_ID}.no_selection.second_line")))
        private val NO_SELECTION: List<TextEntry> = listOf(EMPTY_LINE_1, EMPTY_LINE_2)

        private val LOCATION_X = "gui.${Tempad.MOD_ID}.x"
        private val LOCATION_Y = "gui.${Tempad.MOD_ID}.y"
        private val LOCATION_Z = "gui.${Tempad.MOD_ID}.z"
        private val ANGLE = "gui.${Tempad.MOD_ID}.angle"
        private val LOCATION_DIMENSION = "gui.${Tempad.MOD_ID}.dimension"
    }

    fun updateBlank(showNoSelection: Boolean = true) {
        if (showNoSelection) {
            updateEntries(NO_SELECTION)
        } else {
            updateEntries(emptyList())
        }
    }

    fun update(location: LocationData) {
        updateEntries(
            java.util.List.of(
                TextEntry(Component.literal(location.name), color, shadow),
                TextEntry(Component.translatable(LOCATION_X, Mth.floor(location.x)), color, shadow),
                TextEntry(Component.translatable(LOCATION_Y, Mth.floor(location.y)), color, shadow),
                TextEntry(Component.translatable(LOCATION_Z, Mth.floor(location.z)), color, shadow),
                TextEntry(Component.translatable(ANGLE, location.angle), color, shadow),
                TextEntry(Component.translatable(LOCATION_DIMENSION, location.dimComponent, color, shadow))
            )
        )
    }

    fun updateNameless(pos: GlobalPos) {
        updateEntries(
            java.util.List.of(
                TextEntry(Component.translatable(LOCATION_X, pos.pos().x), color, shadow),
                TextEntry(Component.translatable(LOCATION_Y, pos.pos().y), color, shadow),
                TextEntry(Component.translatable(LOCATION_Z, pos.pos().z), color, shadow),
                TextEntry(
                    Component.translatable(
                        LOCATION_DIMENSION,
                        Component.translatable(pos.dimension().location().toLanguageKey("dimension"))
                    ),
                    color,
                    shadow
                )
            )
        )
    }
}
