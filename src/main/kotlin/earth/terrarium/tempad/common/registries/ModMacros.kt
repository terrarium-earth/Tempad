package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.data.getPinnedLocation
import earth.terrarium.tempad.common.entity.TimedoorEntity

object ModMacros {
    val teleportToPinned = "default".tempadId

    fun init() {
        MacroRegistry[teleportToPinned] = { player, ctx ->
            player.getPinnedLocation(ctx)?.let {
                val (provider, id) = player.pinnedPosition.let { it?.providerId to it?.locationId }
                TimedoorEntity.openTimedoor(player, ctx, provider, id, it)?.let { player.sendOverlayMessage(it)}
            }
        }
    }
}