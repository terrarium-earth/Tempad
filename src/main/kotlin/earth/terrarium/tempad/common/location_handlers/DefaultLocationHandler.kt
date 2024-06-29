package earth.terrarium.tempad.common.location_handlers

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.common.data.locationData
import net.minecraft.world.entity.player.Player
import java.util.*

object DefaultLocationHandler: LocationHandler {
    val settings = ProviderSettings("default".tempadId)

    override fun removeLocation(player: Player, locationId: UUID) {
        player.locationData -= locationId
    }

    override fun getLocations(player: Player): Map<UUID, LocationData> = player.locationData.locations
}