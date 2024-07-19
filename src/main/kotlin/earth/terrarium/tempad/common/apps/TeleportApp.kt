package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.test.ContextHolder
import earth.terrarium.tempad.api.test.SyncableContext
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.menu.TeleportMenu
import earth.terrarium.tempad.common.registries.pinnedLocationData
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class TeleportApp(val ctx: SyncableContext<*>): TempadApp<TeleportData> {
    override fun createMenu(pContainerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return TeleportMenu(
            pContainerId,
            inventory,
            Optional.of(createContent(player as ServerPlayer))
        )
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.teleport")

    override fun createContent(player: ServerPlayer): TeleportData = TeleportData(TempadLocations[player, ctx], player.pinnedLocationData, ctx.holder)

    override fun isEnabled(player: Player): Boolean = true
}

class TeleportData(val locations: Map<ProviderSettings, Map<UUID, LocationData>>, val favoriteLocation: FavoriteLocationAttachment?, ctx: ContextHolder<*>): AppContent<TeleportData>(ctx, codec) {
    constructor(locations: Map<ProviderSettings, Map<UUID, LocationData>>, fav: Optional<FavoriteLocationAttachment>, ctx: ContextHolder<*>): this(locations, fav.getOrNull(), ctx)
    companion object {
        val codec: ByteCodec<TeleportData> = ObjectByteCodec.create(
            ByteCodec.mapOf(
                ProviderSettings.BYTE_CODEC,
                ByteCodec.mapOf(
                    ByteCodec.UUID,
                    LocationData.BYTE_CODEC
                )
            ).fieldOf { it.locations },
            ObjectByteCodec.create(
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ::FavoriteLocationAttachment
            ).optionalFieldOf { Optional.ofNullable(it.favoriteLocation) },
            ContextHolder.codec.fieldOf { it.ctx },
            ::TeleportData
        )
    }
}
