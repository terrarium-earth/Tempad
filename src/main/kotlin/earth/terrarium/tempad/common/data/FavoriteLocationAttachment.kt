package earth.terrarium.tempad.common.data

import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.registries.ModAttachments
import earth.terrarium.tempad.common.utils.OptionalAttachmentDelegate
import net.minecraft.Util
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.attachment.AttachmentHolder
import java.util.*

internal var AttachmentHolder.favoriteLocationData by OptionalAttachmentDelegate(ModAttachments.FAVORITE)

val Player.favoriteLocation: LocationData?
    get() {
        return favoriteLocationData?.let { TempadLocations[it.providerId]?.getLocations(this)?.get(it.locationId) }
    }

data class FavoriteLocationAttachment(val providerId: ResourceLocation, val locationId: UUID) {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("providerId").forGetter(FavoriteLocationAttachment::providerId),
                UUIDUtil.CODEC.fieldOf("locationId").forGetter(FavoriteLocationAttachment::locationId)
            ).apply(instance, ::FavoriteLocationAttachment)
        }

        @JvmField
        val EMPTY = FavoriteLocationAttachment(Tempad.TEMPAD_PROVIDER_SETTINGS.id, Util.NIL_UUID)
    }
}