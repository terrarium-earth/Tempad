package earth.terrarium.tempad.common.data

import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.registries.pinnedLocationData
import net.minecraft.Util
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import java.util.*

val Player.favoriteLocation: LocationData?
    get() {
        return pinnedLocationData?.let { id -> TempadLocations[this, id.providerId]?.let { it[id.locationId] } }
    }

data class FavoriteLocationAttachment(val providerId: ResourceLocation, val locationId: UUID) {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("providerId").forGetter(FavoriteLocationAttachment::providerId),
                UUIDUtil.CODEC.fieldOf("locationId").forGetter(FavoriteLocationAttachment::locationId)
            ).apply(instance, ::FavoriteLocationAttachment)
        }

        val EMPTY = FavoriteLocationAttachment(DefaultLocationHandler.SETTINGS.id, Util.NIL_UUID)
    }

    constructor() : this(DefaultLocationHandler.SETTINGS.id, Util.NIL_UUID)

    fun matches(providerId: ResourceLocation?, locationId: UUID?): Boolean {
        return this.providerId == providerId && this.locationId == locationId
    }
}