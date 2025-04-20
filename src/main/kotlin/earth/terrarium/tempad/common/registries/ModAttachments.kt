package earth.terrarium.tempad.common.registries

import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.common_storage_lib.data.NeoDataLib
import earth.terrarium.common_storage_lib.data.sync.DataSyncSerializer
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.data.MetronomeData
import earth.terrarium.tempad.common.data.TravelHistoryAttachment
import earth.terrarium.tempad.common.location_handlers.AnchorPointsData
import earth.terrarium.tempad.common.location_handlers.PlayerPointsData
import earth.terrarium.tempad.common.registries.ModAttachments.syncedEnergy
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.attachment.AttachmentHolder
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.UUID

object ModAttachments {
    val registry: ResourcefulRegistry<AttachmentType<*>> = ResourcefulRegistries.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tempad.MOD_ID)
    val syncer: ResourcefulRegistry<DataSyncSerializer<*>> = ResourcefulRegistries.create(NeoDataLib.SYNC_SERIALIZERS, Tempad.MOD_ID)

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

    val owner: AttachmentType<GameProfile> by registry.register("owner") {
        attachmentType({GameProfile(null, "null")}) {
            codec = GAME_PROFILE_CODEC.codec()
        }
    }

    val color: AttachmentType<Color> by registry.register("color") {
        attachmentType({ Tempad.ORANGE.withAlpha(0) }) {
            codec = Color.CODEC
        }
    }

    val syncedColor: DataSyncSerializer<Color> by syncer.register("color") {
        DataSyncSerializer.create( { color }, StreamCodecByteCodec.to(Color.BYTE_CODEC))
    }

    val access: AttachmentType<ResourceLocation> by registry.register("access") {
        attachmentType({ PlayerAccessApi.noAccess }) {
            codec = ResourceLocation.CODEC
        }
    }

    val boolAccess: AttachmentType<Boolean> by registry.register("can_access") {
        attachmentType({ false }) {
            codec = Codec.BOOL
        }
    }

    val id: AttachmentType<UUID> by registry.register("id") {
        attachmentType({ UUID.randomUUID() }) {
            codec = UUIDUtil.STRING_CODEC
        }
    }

    val name: AttachmentType<Component> by registry.register("name") {
        attachmentType({ Component.empty() }) {
            codec = ComponentSerialization.CODEC
        }
    }

    val anchorPoints: AttachmentType<AnchorPointsData> by registry.register("anchor_points") {
        attachmentType({ AnchorPointsData(mutableMapOf()) }) {
            codec = AnchorPointsData.codec
        }
    }

    val playerPoints: AttachmentType<PlayerPointsData> by registry.register("player_points") {
        attachmentType({ PlayerPointsData(mutableMapOf()) }) {
            codec = PlayerPointsData.codec
        }
    }

    val metronomeEnergy: AttachmentType<MetronomeData> by registry.register("metronome_energy") {
        attachmentType({ MetronomeData(mapOf(), mapOf()) }) {
            codec = MetronomeData.codec
        }
    }

    val syncedEnergy: DataSyncSerializer<MetronomeData> by syncer.register("metronome_energy") {
        DataSyncSerializer.create( { this@ModAttachments.metronomeEnergy }, StreamCodecByteCodec.to(MetronomeData.byteCodec))
    }

    val yOffset: AttachmentType<Float> by registry.register("yoffset") {
        attachmentType({ 0f }) {
            codec = Codec.FLOAT
        }
    }

    val angle: AttachmentType<Int> by registry.register("angle") {
        attachmentType({ 0 }) {
            codec = Codec.INT
        }
    }
}

var AttachmentHolder.pinnedPosition by ModAttachments.pinnedLocation.optional()
var AttachmentHolder.travelHistory by ModAttachments.travelHistory
var AttachmentHolder.ageUntilAllowedThroughTimedoor by ModAttachments.ageSinceLastTimedoor.optional()

var AttachmentHolder.owner by ModAttachments.owner.optional()
var AttachmentHolder.color by ModAttachments.color.synced(ModAttachments.syncedColor)
var AttachmentHolder.id by ModAttachments.id.optional()
var AttachmentHolder.accessId by ModAttachments.access
var AttachmentHolder.canAccess by ModAttachments.boolAccess
var AttachmentHolder.yOffset by ModAttachments.yOffset
// var AttachmentHolder.name by ModAttachments.name.optional()
var AttachmentHolder.angle by ModAttachments.angle

val anchorPoints by ModAttachments.anchorPoints.serverData
val playerPoints by ModAttachments.playerPoints.serverData
val metronomeEnergy by SyncedServerDataDelegate(ModAttachments.metronomeEnergy, syncedEnergy)