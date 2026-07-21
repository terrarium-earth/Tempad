package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.pinnedPosition
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

data class PortalSetupApp(val ctx: ItemAccessAddress<*>): TempadApp<PortalSetupData> {
    override fun createMenu(pContainerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return ModMenus.PortalSetupMenu(
            pContainerId,
            inventory,
            Optional.of(createContent(player as ServerPlayer))
        )
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.portal_setup")

    override fun createContent(player: ServerPlayer): PortalSetupData {
        val access = ctx.getAccess(player)
        val profile = access.resource.owner ?: player.gameProfile
        return PortalSetupData(
            TempadLocations[profile, access.upgrades!!, access.chronons!!].mapValues { (id, values) ->
                val handler = TempadLocations[profile, access.upgrades!!, access.chronons!!, id]
                return@mapValues values.mapValues { (uuid, position) ->
                    CostAndLocation((handler?.calculateCost(player.level(), player.blockPosition(), uuid) ?: 0), position)
                }
            },
            player.pinnedPosition,
            ctx
        )
    }

    override fun isEnabled(player: Player): Boolean = true
}

class PortalSetupData(val locations: Map<Identifier, Map<UUID, CostAndLocation>>, val favoriteLocation: FavoriteLocationAttachment?, ctx: ItemAccessAddress<*>): AppContent<PortalSetupData>(ctx, true, codec) {
    constructor(locations: Map<Identifier, Map<UUID, CostAndLocation>>, fav: Optional<FavoriteLocationAttachment>, ctx: ItemAccessAddress<*>): this(locations, fav.getOrNull(), ctx)
    companion object {
        val codec: ByteCodec<PortalSetupData> = ObjectByteCodec.create(
            ByteCodec.mapOf(
                ExtraByteCodecs.IDENTIFIER,
                ByteCodec.mapOf(
                    ByteCodec.UUID,
                    CostAndLocation.byteCodec
                )
            ).fieldOf { it.locations },
            ObjectByteCodec.create(
                ExtraByteCodecs.IDENTIFIER.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ::FavoriteLocationAttachment
            ).optionalFieldOf { Optional.ofNullable(it.favoriteLocation) },
            ItemAccessAddress.codec.fieldOf { it.ctx },
            ::PortalSetupData
        )
    }
}