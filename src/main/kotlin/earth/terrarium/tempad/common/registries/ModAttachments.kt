package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.data.NamedGlobalPosAttachment
import earth.terrarium.tempad.common.data.TravelHistoryAttachment
import earth.terrarium.tempad.common.utils.*
import net.neoforged.neoforge.attachment.AttachmentHolder
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModAttachments {
    val registry: ResourcefulRegistry<AttachmentType<*>> = ResourcefulRegistries.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tempad.MOD_ID)

    val locations: AttachmentType<NamedGlobalPosAttachment> by registry.register("saved_positions") {
        attachmentType(::NamedGlobalPosAttachment) {
            codec = NamedGlobalPosAttachment.CODEC
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

var AttachmentHolder.pinnedPosition by ModAttachments.pinnedLocation.optional()
var AttachmentHolder.savedPositions by ModAttachments.locations
var AttachmentHolder.travelHistory by ModAttachments.travelHistory
