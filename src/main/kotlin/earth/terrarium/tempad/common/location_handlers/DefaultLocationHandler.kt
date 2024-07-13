package earth.terrarium.tempad.common.location_handlers

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.context.ContextInstance
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.common.registries.locationData
import java.util.*

class DefaultLocationHandler(val ctx: ContextInstance): LocationHandler {
    companion object {
        val SETTINGS = ProviderSettings("default".tempadId)
    }

    override val locations: Map<UUID, LocationData>
        get() = ctx.player.locationData.locations

    override fun minusAssign(locationId: UUID) {
        ctx.player.locationData -= locationId
    }
}