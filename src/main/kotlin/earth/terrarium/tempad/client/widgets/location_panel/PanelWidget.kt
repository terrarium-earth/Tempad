package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.client.widgets.colored.ColoredList
import java.util.*

class PanelWidget(
    val locations: Map<ProviderSettings, Map<UUID, LocationData>>,
    private val selected: () -> Triple<ProviderSettings, UUID, LocationData>?,
    private val select: (Triple<ProviderSettings, UUID, LocationData>?) -> Unit,
    val filter: () -> String,
    private val isFavorite: (ProviderSettings, UUID) -> Boolean,
) : ColoredList(93, 90) {
    val widgets: MutableMap<ProviderHeader, MutableList<LocationEntry>> =
        locations.map { (provider, locations) ->
            val header = ProviderHeader(provider, this)
            val entries = locations.map { (id, locationData) ->
                LocationEntry(id, locationData) {
                    isFavorite {
                        isFavorite(provider, id)
                    }

                    onClick {
                        if (selected() == Triple(provider, id, locationData)) return@onClick false
                        select(Triple(provider, id, locationData))
                        true
                    }

                    isSelected {
                        selected() == Triple(provider, id, locationData)
                    }
                }
            }.toMutableList()
            header to entries
        }.toMap().toMutableMap()

    fun update() {
        set(getFilteredEntries())
    }

    fun deleteSelected() {
        val (_, id, _) = selected() ?: return
        for ((_, entries) in widgets) {
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