package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.utils.globalPos
import earth.terrarium.tempad.tempadId
import net.minecraft.server.level.ServerLevel

object ModMacros {
    val teleportToPinned = "default".tempadId

    fun init() {
        MacroRegistry[teleportToPinned] = { player, ctx ->
            player.pinnedPosition?.let { pinned ->
                val provider = TempadLocations[player, ctx, pinned.providerId]
                val id = pinned.locationId
                val pos = provider?.get(id) ?: return@let
                val cost = provider.calculateCost(player.level() as? ServerLevel ?: return@let, player.blockPosition(), id)
                TimedoorEntity.openTimedoor(player, ctx, pinned.providerId, id, pos, cost)?.let { player.sendOverlayMessage(it)}
            }
        }
    }
}