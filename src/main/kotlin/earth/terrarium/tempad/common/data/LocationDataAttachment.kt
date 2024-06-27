package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.common.registries.ModAttachments
import earth.terrarium.tempad.common.utils.AttachmentDelegate
import net.minecraft.core.UUIDUtil
import net.neoforged.neoforge.attachment.AttachmentHolder
import java.util.*

var AttachmentHolder.locationData by AttachmentDelegate(ModAttachments.LOCATIONS)

data class LocationDataAttachment(val locations: Map<UUID, LocationData>) {
    companion object {
        @JvmField
        val CODEC: Codec<LocationDataAttachment> = Codec.unboundedMap(UUIDUtil.CODEC, LocationData.CODEC)
            .xmap(::LocationDataAttachment, LocationDataAttachment::locations)
        @JvmField
        val EMPTY = LocationDataAttachment(emptyMap())
    }

    operator fun plus(location: LocationData) = LocationDataAttachment(locations + (UUID.randomUUID() to location))
    operator fun minus(locationId: UUID) = LocationDataAttachment(locations - locationId)
}