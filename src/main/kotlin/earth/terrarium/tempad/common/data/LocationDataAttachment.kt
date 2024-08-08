package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import net.minecraft.core.UUIDUtil
import java.util.*

data class NamedGlobalPosAttachment(val locations: MutableMap<UUID, NamedGlobalPos>) {
    constructor() : this(mutableMapOf())

    companion object {
        @JvmField
        val CODEC: Codec<NamedGlobalPosAttachment> = Codec.unboundedMap(UUIDUtil.STRING_CODEC, NamedGlobalPos.codec)
            .xmap({ NamedGlobalPosAttachment(it.toMutableMap()) }, NamedGlobalPosAttachment::locations)
    }

    operator fun plusAssign(location: NamedGlobalPos) {
        locations[UUID.randomUUID()] = location
    }

    operator fun minusAssign(locationId: UUID) {
        locations.remove(locationId)
    }
}