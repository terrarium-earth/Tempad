package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.olympus.client.components.lists.EntryListWidget
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.ProviderSettings
import java.util.*
import kotlin.reflect.KMutableProperty

class PanelWidget(
    val locations: Map<ProviderSettings, Map<UUID, LocationData>>,
    private val selected: () -> Triple<ProviderSettings, UUID, LocationData>?,
    private val select: (Triple<ProviderSettings, UUID, LocationData>?) -> Unit,
    val filter: () -> String,
    private val isFavorite: (ProviderSettings, UUID) -> Boolean,
) : EntryListWidget<Any>(92, 92) {
    val widgets: MutableMap<ProviderHeader, MutableList<LocationEntry>> = locations.mapKeys { (provider) -> ProviderHeader(provider) }.mapValues { (provider, locations) ->
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

    override fun update() {
        clear()
        addFilteredEntries()
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

    private fun addFilteredEntries() {
        for ((provider, entries) in widgets) {
            val filtered = entries.filter { it.data.name.contains(filter(), ignoreCase = true) }
            if (filtered.isEmpty()) continue
            add(provider)
            if (provider.hidden) continue
            filtered.forEach { add(it) }
        }
    }
}