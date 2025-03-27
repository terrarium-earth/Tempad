package earth.terrarium.tempad.common.location_handlers

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.locations.namedGlobalVec3
import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.enabled
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import java.util.UUID

class PlayerHandler(val player: GameProfile, val upgrades: UpgradeHandler) : LocationHandler {
    override val locations: Map<UUID, NamedGlobalVec3>
        get() {
            if (Tempad.playerUpgrade !in upgrades) return emptyMap()
            return Tempad.server?.let {
                it.playerList.players
                    .filter { it.uuid != player.id }
                    .filter { ContextRegistry.locate(it) { it.item === ModItems.statusEmitter && it.enabled } != null }
                    .associate { it.uuid to it.namedGlobalVec3 }
            } ?: emptyMap()
        }

    override fun minusAssign(locationId: UUID) {
        // NO-OP
    }

    override fun getSerializable(locationId: UUID): LocationGetter? {
        val pos = Tempad.server?.playerList?.getPlayer(locationId)?.gameProfile ?: return null
        return IndirectLocation(player, Component.translatable("locations.tempad.player", Component.literal(pos.name).withStyle(
            ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY), ID, locationId)
    }

    companion object {
        val ID = "player".tempadId
    }
}