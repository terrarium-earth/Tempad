package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.api.macro.TempadMacro
import earth.terrarium.tempad.common.data.favoriteLocation
import earth.terrarium.tempad.common.entity.TimedoorEntity

object ModMacros {
    val DEFAULT_MACRO: TempadMacro = { player, stack, slot ->
        player.favoriteLocation?.let {
            TimedoorEntity.openTimedoor(player, slot, it)
        }
    }

    val DEFAULT_MACRO_ID = "default_macro".tempadId

    fun init() {
        MacroRegistry.register(DEFAULT_MACRO_ID, DEFAULT_MACRO)
    }
}