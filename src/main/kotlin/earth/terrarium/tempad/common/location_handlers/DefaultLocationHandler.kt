package earth.terrarium.tempad.common.location_handlers

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.locations.DirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.common.registries.playerPoints
import earth.terrarium.tempad.tempadId
import java.util.*

class DefaultLocationHandler(val gameProfile: GameProfile) : LocationHandler {
    companion object {
        val ID = "default".tempadId
    }

    override val locations: Map<UUID, NamedGlobalVec3> get() = playerPoints[gameProfile.id]

    override fun minusAssign(locationId: UUID) {
        playerPoints[gameProfile.id] -= locationId
    }

    operator fun plusAssign(location: NamedGlobalVec3) {
        playerPoints[gameProfile.id] += UUID.randomUUID() to location
    }

    override fun getSerializable(locationId: UUID): LocationGetter? {
        return get(locationId)?.let { DirectLocation(it) }
    }
}