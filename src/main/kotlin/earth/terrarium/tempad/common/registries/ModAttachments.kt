package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.data.LocationDataAttachment
import earth.terrarium.tempad.common.utils.attachmentType
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModAttachments {
    val REGISTRY = NeoForgeRegistries.ATTACHMENT_TYPES.createRegistry(Tempad.MOD_ID)

    val LOCATIONS: AttachmentType<LocationDataAttachment> by REGISTRY.register("locations") {
        attachmentType(LocationDataAttachment::EMPTY) {
            codec = LocationDataAttachment.CODEC
            copyOnDeath()
        }
    }

    val FAVORITE: AttachmentType<FavoriteLocationAttachment> by REGISTRY.register("favorite") {
        attachmentType(FavoriteLocationAttachment::EMPTY) {
            codec = FavoriteLocationAttachment.CODEC
            copyOnDeath()
        }
    }
}