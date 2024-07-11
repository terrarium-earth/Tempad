package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.data.LocationDataAttachment
import earth.terrarium.tempad.common.data.TravelHistoryAttachment
import earth.terrarium.tempad.common.utils.*
import net.neoforged.neoforge.attachment.AttachmentHolder
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModAttachments {
    val registry: ResourcefulRegistry<AttachmentType<*>> = ResourcefulRegistries.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tempad.MOD_ID)

    val locations: AttachmentType<LocationDataAttachment> by registry.register("locations") {
        attachmentType(::LocationDataAttachment) {
            codec = LocationDataAttachment.CODEC
            copyOnDeath()
        }
    }

    val pinnedLocation: AttachmentType<FavoriteLocationAttachment> by registry.register("pinned_location") {
        attachmentType(::FavoriteLocationAttachment) {
            codec = FavoriteLocationAttachment.CODEC
            copyOnDeath()
        }
    }

    val travelHistory: AttachmentType<TravelHistoryAttachment> by registry.register("travel_history") {
        attachmentType(::TravelHistoryAttachment) {
            codec = TravelHistoryAttachment.CODEC
            copyOnDeath()
        }
    }
}

var AttachmentHolder.pinnedLocationData by ModAttachments.pinnedLocation.optional()
var AttachmentHolder.locationData by ModAttachments.locations
var AttachmentHolder.travelHistory by ModAttachments.travelHistory
