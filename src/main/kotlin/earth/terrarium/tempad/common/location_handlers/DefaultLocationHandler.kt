package earth.terrarium.tempad.common.location_handlers

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.common.registries.locationData
import net.minecraft.world.entity.player.Player
import java.util.*

class DefaultLocationHandler(val player: Player): LocationHandler {
    companion object {
        val SETTINGS = ProviderSettings("default".tempadId)
    }

    override val locations: Map<UUID, LocationData>
        get() = player.locationData.locations

    override fun minusAssign(locationId: UUID) {
        player.locationData -= locationId
    }
}