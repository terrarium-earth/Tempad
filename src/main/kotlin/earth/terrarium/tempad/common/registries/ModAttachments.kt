package earth.terrarium.tempad.common.registries

import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.EmptyLocation
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
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

    val ageSinceLastTimedoor: AttachmentType<Int> by registry.register("age_until_allowed_through_timedoor") {
        attachmentType({0}) {}
    }

    val tempadOwner: AttachmentType<GameProfile> by registry.register("tempad_owner") {
        attachmentType({GameProfile(null, "null")}) {
            codec = GAME_PROFILE_CODEC.codec()
        }
    }

    val targetLocation: AttachmentType<NamedGlobalPos> by registry.register("target_location") {
        attachmentType({ EmptyLocation }) {
            codec = NamedGlobalPos.codec
        }
    }

    val chrononContent: AttachmentType<Int> by registry.register("chronon_content") {
        attachmentType({0}) {
            codec = Codec.INT
        }
    }
}

var AttachmentHolder.pinnedPosition by ModAttachments.pinnedLocation.optional()
var AttachmentHolder.savedPositions by ModAttachments.locations
var AttachmentHolder.travelHistory by ModAttachments.travelHistory
var AttachmentHolder.ageUntilAllowedThroughTimedoor by ModAttachments.ageSinceLastTimedoor.optional()

var AttachmentHolder.tempadOwner by ModAttachments.tempadOwner.optional()
var AttachmentHolder.targetLocation by ModAttachments.targetLocation.optional()
var AttachmentHolder.chrononContent by ModAttachments.chrononContent