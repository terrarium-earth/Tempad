package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.locations.*
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.location_handlers.PlayerHandler
import earth.terrarium.tempad.common.location_handlers.AnchorPointsHandler

object ModLocations {
    fun init() {
        TempadLocations[DefaultLocationHandler.ID] = ::DefaultLocationHandler
        TempadLocations[AnchorPointsHandler.id] = { player, _ -> AnchorPointsHandler(player) }
        TempadLocations[PlayerHandler.ID] = ::PlayerHandler

        LocationTypeRegistry.register(StaticNamedGlobalPos.type)
        LocationTypeRegistry.register(PlayerPos.type)
        LocationTypeRegistry.register(EmptyLocation.type)
    }
}