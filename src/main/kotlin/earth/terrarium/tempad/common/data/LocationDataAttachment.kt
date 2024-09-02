package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import net.minecraft.core.UUIDUtil
import java.util.*

data class NamedGlobalPosAttachment(val locations: MutableMap<UUID, NamedGlobalVec3>) {
    constructor() : this(mutableMapOf())

    companion object {
        @JvmField
        val CODEC: Codec<NamedGlobalPosAttachment> = Codec.unboundedMap(UUIDUtil.STRING_CODEC, NamedGlobalVec3.CODEC.codec())
            .xmap({ NamedGlobalPosAttachment(it.toMutableMap()) }, NamedGlobalPosAttachment::locations)
    }

    operator fun plusAssign(location: NamedGlobalVec3) {
        locations[UUID.randomUUID()] = location
    }

    operator fun minusAssign(locationId: UUID) {
        locations.remove(locationId)
    }
}