package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.tempad.api.locations.ClientDisplay
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.client.screen.Sorting
import earth.terrarium.tempad.client.widgets.colored.ColoredList
import earth.terrarium.tempad.common.utils.dimDisplay
import net.minecraft.network.chat.Component
import java.util.*

class PanelWidget(
    val locations: Map<ProviderSettings, Map<UUID, ClientDisplay>>,
    private val selected: () -> Triple<ProviderSettings, UUID, ClientDisplay>?,
    private val select: (Triple<ProviderSettings, UUID, ClientDisplay>?) -> Unit,
    val filter: () -> String,
    private val isFavorite: (ProviderSettings, UUID) -> Boolean,
) : ColoredList(111, 74) {
    val widgets: MutableMap<ProviderHeader, MutableList<LocationEntry>> = mutableMapOf()

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

    fun sortBy(sorting: Sorting) {
        widgets.clear()
        when (sorting) {
            Sorting.TYPE -> {
                widgets.putAll(locations.map { (provider, locations) ->
                    val header = ProviderHeader(provider, this)
                    val entries = locations.map { (id, display) -> createEntry(provider, id, display)
                    }.toMutableList()
                    header to entries
                }.toMap().toMutableMap())
            }

            Sorting.ALPHABETICAL -> {
                locations.entries
                    .flatMap { (provider, locations) -> locations.entries.map { provider to it } }
                    .forEach { (provider, entry) ->
                        val (id, display) = entry
                        val letter = display.name.string.first().toString().uppercase(Locale.ROOT)
                        val header = ProviderHeader(Component.literal(letter), letter, this)
                        widgets.getOrPut(header) { mutableListOf() } += createEntry(provider, id, display)
                    }
            }

            Sorting.DIMENSION -> {
                locations.entries
                    .flatMap { (provider, locations) -> locations.entries.map { provider to it } }
                    .forEach { (provider, entry) ->
                        val (id, display) = entry
                        val header = ProviderHeader(display.pos.dimDisplay, display.pos.dimension.toString(), this)
                        widgets.getOrPut(header) { mutableListOf() } += createEntry(provider, id, display)
                    }
            }
        }
        update()
    }

    fun createEntry(provider: ProviderSettings, id: UUID, display: ClientDisplay): LocationEntry {
        return LocationEntry(id, display) {
            isFavorite {
                isFavorite(provider, id)
            }

            onClick {
                if (selected() == Triple(provider, id, display)) return@onClick false
                select(Triple(provider, id, display))
                true
            }

            isSelected {
                selected() == Triple(provider, id, display)
            }
        }
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