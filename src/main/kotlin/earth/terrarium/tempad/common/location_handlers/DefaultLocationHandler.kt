package earth.terrarium.tempad.common.location_handlers

import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.savedPositions
import earth.terrarium.tempad.common.registries.staticLocation
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.world.entity.player.Player
import java.util.*

class DefaultLocationHandler(val player: Player, val ctx: SyncableContext<*>): LocationHandler {
    companion object {
        val SETTINGS = ProviderSettings("default".tempadId)
    }

    override val locations: Map<UUID, NamedGlobalPos> by player.savedPositions::locations

    override fun minusAssign(locationId: UUID) {
        player.savedPositions -= locationId
    }
}