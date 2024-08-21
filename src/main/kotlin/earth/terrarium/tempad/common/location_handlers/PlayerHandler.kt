package earth.terrarium.tempad.common.location_handlers

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.PlayerPos
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.enabled
import earth.terrarium.tempad.common.registries.installedUpgrades
import earth.terrarium.tempad.tempadId
import net.minecraft.world.entity.player.Player
import java.util.UUID

class PlayerHandler(val player: Player, val ctx: SyncableContext<*>) : LocationHandler {
    override val locations: Map<UUID, NamedGlobalPos>
        get() {
            if (Tempad.playerUpgrade !in ctx.stack.installedUpgrades) return emptyMap()
            return Tempad.server?.let {
                it.playerList.players
                    .filter { it.uuid != player.uuid }
                    .filter { ContextRegistry.locate(it) { it.item === ModItems.statusEmitter && it.enabled } != null }
                    .map { it.uuid to PlayerPos(it.gameProfile) }
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