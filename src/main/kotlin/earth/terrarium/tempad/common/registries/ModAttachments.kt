package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.data.LocationDataAttachment
import earth.terrarium.tempad.common.utils.attachmentType
import earth.terrarium.tempad.common.utils.codec
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModAttachments {
    val REGISTRY: ResourcefulRegistry<AttachmentType<*>> = ResourcefulRegistries.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tempad.MOD_ID)

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