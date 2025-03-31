package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.pinnedPosition
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

data class PortalSetupApp(val ctx: SyncableContext<*>): TempadApp<PortalSetupData> {
    override fun createMenu(pContainerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return ModMenus.PortalSetupMenu(
            pContainerId,
            inventory,
            Optional.of(createContent(player as ServerPlayer))
        )
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.portal_setup")

    override fun createContent(player: ServerPlayer): PortalSetupData = PortalSetupData(TempadLocations[player, ctx], player.pinnedPosition, ctx.holder)

    override fun isEnabled(player: Player): Boolean = true
}

class PortalSetupData(val locations: Map<ResourceLocation, Map<UUID, NamedGlobalVec3>>, val favoriteLocation: FavoriteLocationAttachment?, ctx: ContextHolder<*>): AppContent<PortalSetupData>(ctx, true, codec) {
    constructor(locations: Map<ResourceLocation, Map<UUID, NamedGlobalVec3>>, fav: Optional<FavoriteLocationAttachment>, ctx: ContextHolder<*>): this(locations, fav.getOrNull(), ctx)
    companion object {
        val codec: ByteCodec<PortalSetupData> = ObjectByteCodec.create(
            ByteCodec.mapOf(
                ExtraByteCodecs.RESOURCE_LOCATION,
                ByteCodec.mapOf(
                    ByteCodec.UUID,
                    NamedGlobalVec3.BYTE_CODEC
                )
            ).fieldOf { it.locations },
            ObjectByteCodec.create(
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ::FavoriteLocationAttachment
            ).optionalFieldOf { Optional.ofNullable(it.favoriteLocation) },
            ContextHolder.codec.fieldOf { it.ctx },
            ::PortalSetupData
        )
    }
}