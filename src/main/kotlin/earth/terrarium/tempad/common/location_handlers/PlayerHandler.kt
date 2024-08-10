package earth.terrarium.tempad.common.location_handlers

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.PlayerPos
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.enabled
import earth.terrarium.tempad.tempadId
import net.minecraft.world.entity.player.Player
import java.util.UUID

class PlayerHandler(val player: Player, ctx: SyncableContext<*>) : LocationHandler {
    override val locations: Map<UUID, NamedGlobalPos>
        get() = Tempad.server?.let {
            it.playerList.players
                .filter { it.uuid != player.uuid }
                .filter { ContextRegistry.locate(it) { it.item === ModItems.temporalBeacon && it.enabled } != null }
                .map { it.uuid to PlayerPos(it.gameProfile) }
                .toMap()
        } ?: emptyMap()

    override fun minusAssign(locationId: UUID) {
        // NO-OP
    }

    companion object {
        val SETTINGS = ProviderSettings("player".tempadId, deletable = false)
    }
}