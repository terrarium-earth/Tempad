package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.data.pinnedLocation
import earth.terrarium.tempad.common.entity.TimedoorEntity

object ModMacros {
    val teleportToPinned = "default".tempadId

    fun init() {
        MacroRegistry[teleportToPinned] = { ctx ->
            ctx.pinnedLocation?.let {
                TimedoorEntity.openTimedoor(ctx, it)
            }
        }
    }
}