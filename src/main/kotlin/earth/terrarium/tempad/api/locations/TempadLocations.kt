package earth.terrarium.tempad.api.locations

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.tva_device.TempadDevice
import net.minecraft.resources.ResourceLocation
import java.util.*

interface LocationHandler {
    val locations: Map<UUID, NamedGlobalVec3>

    operator fun minusAssign(locationId: UUID)

    operator fun get(locationId: UUID): NamedGlobalVec3? = locations[locationId]
}

typealias LocationProvider = (GameProfile, TempadDevice) -> LocationHandler

object TempadLocations {
    val registry: Map<ResourceLocation, LocationProvider>
        field = mutableMapOf()

    @JvmStatic
    @JvmName("register")
    operator fun set(settings: ResourceLocation, provider: LocationProvider) {
        registry[settings] = provider
    }

    @JvmStatic
    operator fun get(id: ResourceLocation): LocationProvider? = registry[id]

    @JvmStatic
    operator fun get(player: GameProfile, ctx: TempadDevice, id: ResourceLocation): LocationHandler? =
        registry[id]?.let { it(player, ctx) }

    @JvmStatic
    operator fun get(player: GameProfile, ctx: TempadDevice): Map<ResourceLocation, Map<UUID, NamedGlobalVec3>> =
        registry.mapValues { it.value(player, ctx).locations }
}