package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.client.widgets.colored.ColoredList
import net.minecraft.client.gui.GuiGraphics
import java.util.*

class PanelWidget(
    val locations: Map<ProviderSettings, Map<UUID, LocationData>>,
    private val selected: () -> Triple<ProviderSettings, UUID, LocationData>?,
    private val select: (Triple<ProviderSettings, UUID, LocationData>?) -> Unit,
    val filter: () -> String,
    private val isFavorite: (ProviderSettings, UUID) -> Boolean,
) : ColoredList(92, 92) {
    val widgets: MutableMap<ProviderHeader, MutableList<LocationEntry>> =
        locations.mapKeys { (provider) -> ProviderHeader(provider, this) }.mapValues { (provider, locations) ->
            return@mapValues locations.map { (id, locationData) ->
                LocationEntry(id, locationData) {
                    isFavorite {
                        isFavorite(provider.settings, id)
                    }

                    onClick {
                        if (selected() == Triple(provider.settings, id, locationData)) return@onClick false
                        select(Triple(provider.settings, id, locationData))
                        true
                    }

                    isSelected {
                        selected() == Triple(provider.settings, id, locationData)
                    }
                }
            }.toMutableList()
        }.toMutableMap()

    fun update() {
        set(getFilteredEntries())
    }

    fun deleteSelected() {
        val (settings, id, _) = selected() ?: return
        for ((header, entries) in widgets) {
            if (header.settings != settings) continue
            val entry = entries.find { it.id == id }
            if (entry != null) {
                entries -= entry
                break
            }
        }
        select(null)
        update()
    }

    private fun getFilteredEntries(): MutableList<Item> {
        val list = mutableListOf<Item>()
        for ((provider, entries) in widgets) {
            val filtered = entries.filter { it.data.name.contains(filter(), ignoreCase = true) }
            if (filtered.isEmpty()) continue
            list += provider
            if (provider.hidden) continue
            filtered.forEach { list += it }
        }
        return list
    }
}