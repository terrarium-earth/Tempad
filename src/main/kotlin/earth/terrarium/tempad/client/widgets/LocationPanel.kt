package earth.terrarium.tempad.client.widgets

import com.teamresourceful.resourcefullib.client.components.selection.SelectionList
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.ProviderSettings
import net.minecraft.network.chat.Component
import java.util.*

class LocationPanel(
    x: Int, y: Int, width: Int, height: Int,
    val locations: Map<ProviderSettings, Map<UUID, LocationData>>,
    private val isFavorite: (ProviderSettings?, UUID?) -> Boolean,
    onSelection: (TextEntry?) -> Unit
) : SelectionList<TextEntry>(x, y, width, height, 11, onSelection) {
    companion object {
        private val EMPTY: List<TextEntry> = listOf(
            TextEntry(Component.translatable("gui.${Tempad.MOD_ID}.no_locations.first_line")),
            TextEntry(Component.translatable("gui.${Tempad.MOD_ID}.no_locations.second_line")),
        )
    }

    init {
        for ((provider, locations) in locations) {
            addEntry(TextEntry(Component.translatable(provider.id.toLanguageKey("provider"))))
            for ((id, locationData) in locations) {
                val entry = TextEntry(provider, id, locationData, isFavorite)
                this.addEntry(entry)
            }
        }
    }

    fun select(settings: ProviderSettings, selected: UUID) {
        for (entry in this.children()) {
            if (entry.settings == settings && entry.id == selected) {
                this.setSelected(entry)
                return
            }
        }
    }

    fun update(text: String) {
        if (locations.isEmpty()) {
            updateEntries(EMPTY)
        } else {
            val newEntries = mutableListOf<TextEntry>()
            for ((provider, locations) in locations) {
                newEntries += (TextEntry(Component.translatable(provider.id.toLanguageKey("provider"))))
                for ((id, locationData) in locations) {
                    if (!locationData.name.contains(text, ignoreCase = true)) continue
                    newEntries += TextEntry(provider, id, locationData, isFavorite)
                }
            }
        }
    }
}