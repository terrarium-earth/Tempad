package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.tempad.api.locations.ClientDisplay
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.client.widgets.colored.ColoredList
import java.util.*

class PanelWidget(
    val locations: Map<ProviderSettings, Map<UUID, ClientDisplay>>,
    private val selected: () -> Triple<ProviderSettings, UUID, ClientDisplay>?,
    private val select: (Triple<ProviderSettings, UUID, ClientDisplay>?) -> Unit,
    val filter: () -> String,
    private val isFavorite: (ProviderSettings, UUID) -> Boolean,
) : ColoredList(111, 74) {
    val widgets: MutableMap<ProviderHeader, MutableList<LocationEntry>> =
        locations.map { (provider, locations) ->
            val header = ProviderHeader(provider, this)
            val entries = locations.map { (id, display) ->
                LocationEntry(id, display) {
                    isFavorite {
                        isFavorite(provider, id)
                    }

                    onClick {
                        if (selected() == Triple(provider, id, display)) return@onClick false
                        select(Triple(provider, id, display))
                        true
                    }

                    isSelected {
                        selected() == Triple(provider, id, StaticNamedGlobalPos)
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
            val filtered = entries.filter { it.data.name.string.contains(filter(), ignoreCase = true) }
            if (filtered.isEmpty()) continue
            list += provider
            if (provider.hidden) continue
            filtered.forEach { list += it }
        }
        return list
    }
}