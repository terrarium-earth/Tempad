package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import net.minecraft.core.UUIDUtil
import org.lwjgl.system.linux.Stat
import java.util.*

data class NamedGlobalPosAttachment(val locations: MutableMap<UUID, StaticNamedGlobalPos>) {
    constructor() : this(mutableMapOf())

    companion object {
        @JvmField
        val CODEC: Codec<NamedGlobalPosAttachment> = Codec.unboundedMap(UUIDUtil.STRING_CODEC, StaticNamedGlobalPos.CODEC.codec())
            .xmap({ NamedGlobalPosAttachment(it.toMutableMap()) }, NamedGlobalPosAttachment::locations)
    }

    operator fun plusAssign(location: StaticNamedGlobalPos) {
        locations[UUID.randomUUID()] = location
    }

    operator fun minusAssign(locationId: UUID) {
        locations.remove(locationId)
    }
}