package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.data.getPinnedLocation
import earth.terrarium.tempad.common.entity.TimedoorEntity

object ModMacros {
    val teleportToPinned = "default".tempadId

    fun init() {
        MacroRegistry[teleportToPinned] = { player, ctx ->
            player.getPinnedLocation(ctx)?.let {
                TimedoorEntity.openTimedoor(player, ctx, it)
            }
        }
    }
}