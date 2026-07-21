package earth.terrarium.tempad.common.data

import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.registries.pinnedPosition
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.Identifier
import net.minecraft.util.Util
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.transfer.access.ItemAccess
import java.util.*

data class FavoriteLocationAttachment(val providerId: Identifier, val locationId: UUID) {
    companion object {
        val CODEC = RecordCodecBuilder.create { instance ->
            instance.group(
                Identifier.CODEC.fieldOf("providerId").forGetter(FavoriteLocationAttachment::providerId),
                UUIDUtil.CODEC.fieldOf("locationId").forGetter(FavoriteLocationAttachment::locationId)
            ).apply(instance, ::FavoriteLocationAttachment)
        }

        val EMPTY = FavoriteLocationAttachment(DefaultLocationHandler.ID, Util.NIL_UUID)
    }

    constructor() : this(DefaultLocationHandler.ID, Util.NIL_UUID)

    fun matches(providerId: Identifier?, locationId: UUID?): Boolean {
        return this.providerId == providerId && this.locationId == locationId
    }
}