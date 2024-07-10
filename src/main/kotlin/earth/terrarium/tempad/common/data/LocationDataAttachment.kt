package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import earth.terrarium.tempad.api.locations.LocationData
import net.minecraft.core.UUIDUtil
import java.util.*

data class LocationDataAttachment(val locations: MutableMap<UUID, LocationData>) {
    constructor() : this(mutableMapOf())

    companion object {
        @JvmField
        val CODEC: Codec<LocationDataAttachment> = Codec.unboundedMap(UUIDUtil.STRING_CODEC, LocationData.CODEC)
            .xmap({ LocationDataAttachment(it.toMutableMap()) }, LocationDataAttachment::locations)
    }

    operator fun plusAssign(location: LocationData) {
        locations[UUID.randomUUID()] = location
    }

    operator fun minusAssign(locationId: UUID) {
        locations.remove(locationId)
    }
}