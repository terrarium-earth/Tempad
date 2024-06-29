package earth.terrarium.tempad.common.location_handlers

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.ProviderSettings
import net.minecraft.world.entity.player.Player
import java.util.*

object WarpsHandler: LocationHandler {
    val settings = ProviderSettings("warps".tempadId, downloadable = false)

    override fun removeLocation(player: Player, locationId: UUID) {
        // Operation not supported
    }

    override fun getLocations(player: Player): Map<UUID, LocationData> {
        TODO("Not yet implemented")
    }
}