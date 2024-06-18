package earth.terrarium.tempad.api

import earth.terrarium.tempad.common.data.LocationData
import earth.terrarium.tempad.common.data.favoriteLocation
import earth.terrarium.tempad.common.data.locationData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import java.util.UUID

@JvmRecord
data class ProviderSettings(val id: ResourceLocation, val exportable: Boolean = true, val downloadable: Boolean = true)

typealias LocationProvider = (Player) -> Map<UUID, LocationData>

object TempadLocations: Iterable<Map.Entry<ProviderSettings, LocationProvider>> {
    private val providers: MutableMap<ProviderSettings, LocationProvider> = mutableMapOf()

    @JvmStatic
    fun register(settings: ProviderSettings, provider: LocationProvider) {
        providers[settings] = provider
    }

    @JvmStatic
    operator fun get(settings: ProviderSettings): LocationProvider? = providers[settings]

    @JvmStatic
    operator fun get(id: ResourceLocation): LocationProvider? = providers.keys.find { it.id == id }?.let { providers[it] }

    @JvmStatic
    operator fun get(player: Player): LocationHandler = LocationHandler(player)

    override operator fun iterator(): Iterator<Map.Entry<ProviderSettings, LocationProvider>> = providers.iterator()
}

class LocationHandler(val player: Player): Iterable<Pair<ProviderSettings, Map<UUID, LocationData>>>{
    var favorite
        get() = player.favoriteLocation
        set(value) { player.favoriteLocation = value }

    operator fun get(settings: ProviderSettings): Map<UUID, LocationData> = TempadLocations[settings]?.invoke(player) ?: emptyMap()

    operator fun get(id: ResourceLocation): Map<UUID, LocationData> = TempadLocations[id]?.invoke(player) ?: emptyMap()

    fun getAll(): Map<ProviderSettings, Map<UUID, LocationData>> = TempadLocations.associate { (settings, provider) -> settings to provider(player) }

    @JvmName("add")
    operator fun plusAssign(location: LocationData) {
        player.locationData += location
    }

    @JvmName("remove")
    operator fun minusAssign(locationId: UUID) {
        player.locationData -= locationId
    }

    override fun iterator(): Iterator<Pair<ProviderSettings, Map<UUID, LocationData>>> = TempadLocations
        .asSequence()
        .map { (settings, provider) -> settings to provider(player) }
        .iterator()
}
