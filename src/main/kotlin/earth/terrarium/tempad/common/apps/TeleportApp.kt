package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.PositionSnapshot
import earth.terrarium.tempad.api.locations.positionSnapshot
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.registries.ModMenus.TeleportMenu
import earth.terrarium.tempad.common.registries.pinnedPosition
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
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

    override fun createContent(player: ServerPlayer): TeleportData = TeleportData(TempadLocations[player, ctx].mapValues { map -> map.value.mapNotNull { pos -> pos.value.positionSnapshot?.let { pos.key to it } }.toMap() }, player.pinnedPosition, ctx.holder)

    override fun isEnabled(player: Player): Boolean = true
}

class TeleportData(val locations: Map<ResourceLocation, Map<UUID, PositionSnapshot>>, val favoriteLocation: FavoriteLocationAttachment?, ctx: ContextHolder<*>): AppContent<TeleportData>(ctx, codec) {
    constructor(locations: Map<ResourceLocation, Map<UUID, PositionSnapshot>>, fav: Optional<FavoriteLocationAttachment>, ctx: ContextHolder<*>): this(locations, fav.getOrNull(), ctx)
    companion object {
        val codec: ByteCodec<TeleportData> = ObjectByteCodec.create(
            ByteCodec.mapOf(
                ExtraByteCodecs.RESOURCE_LOCATION,
                ByteCodec.mapOf(
                    ByteCodec.UUID,
                    PositionSnapshot.byteCodec
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
