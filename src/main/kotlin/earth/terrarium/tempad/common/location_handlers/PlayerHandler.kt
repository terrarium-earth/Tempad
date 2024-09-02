package earth.terrarium.tempad.common.location_handlers

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.locations.namedGlobalVec3
import earth.terrarium.tempad.api.tva_device.TempadDevice
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.enabled
import earth.terrarium.tempad.tempadId
import net.minecraft.world.entity.player.Player
import java.util.UUID

class PlayerHandler(val player: GameProfile, val tempad: TempadDevice) : LocationHandler {
    override val locations: Map<UUID, NamedGlobalVec3>
        get() {
            if (Tempad.playerUpgrade !in tempad) return emptyMap()
            return Tempad.server?.let {
                it.playerList.players
                    .filter { it.uuid != player.id }
                    .filter { ContextRegistry.locate(it) { it.item === ModItems.statusEmitter && it.enabled } != null }
                    .map { it.uuid to it.namedGlobalVec3 }
                    .toMap()
            } ?: emptyMap()
        }

    override fun minusAssign(locationId: UUID) {
        // NO-OP
    }

    companion object {
        val ID = "player".tempadId
    }
}