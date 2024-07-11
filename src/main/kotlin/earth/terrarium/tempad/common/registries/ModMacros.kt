package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.data.favoriteLocation
import earth.terrarium.tempad.common.entity.TimedoorEntity

object ModMacros {
    val teleportToPinned = "default".tempadId

    fun init() {
        MacroRegistry[teleportToPinned] = { player, stack, slot ->
            player.favoriteLocation?.let {
                TimedoorEntity.openTimedoor(player, slot, it)
            }
        }
    }
}