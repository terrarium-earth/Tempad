package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.locations.LocationTypeRegistry
import earth.terrarium.tempad.api.locations.PlayerPos
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.location_handlers.PlayerHandler
import earth.terrarium.tempad.common.location_handlers.WarpsHandler

object ModLocations {
    fun init() {
        TempadLocations[DefaultLocationHandler.SETTINGS] = ::DefaultLocationHandler
        TempadLocations[WarpsHandler.SETTINGS] = ::WarpsHandler
        TempadLocations[PlayerHandler.SETTINGS] = ::PlayerHandler

        LocationTypeRegistry.register(StaticNamedGlobalPos.type)
        LocationTypeRegistry.register(PlayerPos.type)
    }
}