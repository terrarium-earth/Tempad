package earth.terrarium.tempad.common.location_handlers

import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.common.registries.savedPositions
import earth.terrarium.tempad.tempadId
import net.minecraft.world.entity.player.Player
import java.util.*

class DefaultLocationHandler(val player: Player, val ctx: SyncableContext<*>) : LocationHandler {
    companion object {
        val ID = "default".tempadId
    }

    override val locations: Map<UUID, NamedGlobalPos> by player.savedPositions::locations

    override fun minusAssign(locationId: UUID) {
        player.savedPositions -= locationId
    }
}