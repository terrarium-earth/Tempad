package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.registries.ModMenus.TeleportMenu
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.pinnedPosition
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class TeleportApp(val ctx: ItemAccessAddress<*>): TempadApp<TeleportData> {
    override fun createMenu(pContainerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return TeleportMenu(
            pContainerId,
            inventory,
            Optional.of(createContent(player as ServerPlayer))
        )
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.teleport")

    override fun createContent(player: ServerPlayer): TeleportData {
        val access = ctx.getAccess(player)
        val profile = access.resource.owner ?: player.gameProfile
        return TeleportData(TempadLocations[profile, access.upgrades!!, access.chronons!!], player.pinnedPosition, ctx)
    }

    override fun isEnabled(player: Player): Boolean = true
}

class TeleportData(val locations: Map<Identifier, Map<UUID, NamedGlobalVec3>>, val favoriteLocation: FavoriteLocationAttachment?, ctx: ItemAccessAddress<*>): AppContent<TeleportData>(ctx, false, codec) {
    constructor(locations: Map<Identifier, Map<UUID, NamedGlobalVec3>>, fav: Optional<FavoriteLocationAttachment>, ctx: ItemAccessAddress<*>): this(locations, fav.getOrNull(), ctx)
    companion object {
        val codec: ByteCodec<TeleportData> = ObjectByteCodec.create(
            ByteCodec.mapOf(
                ExtraByteCodecs.IDENTIFIER,
                ByteCodec.mapOf(
                    ByteCodec.UUID,
                    NamedGlobalVec3.BYTE_CODEC
                )
            ).fieldOf { it.locations },
            ObjectByteCodec.create(
                ExtraByteCodecs.IDENTIFIER.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ::FavoriteLocationAttachment
            ).optionalFieldOf { Optional.ofNullable(it.favoriteLocation) },
            ItemAccessAddress.codec.fieldOf { it.ctx },
            ::TeleportData
        )
    }
}
