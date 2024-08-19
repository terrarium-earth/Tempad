package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.sizing.DefaultSizing
import earth.terrarium.tempad.api.sizing.SizingRegistry

object ModSizing {
    fun init() {
        SizingRegistry.register(DefaultSizing.DEFAULT.type)
        SizingRegistry.register(DefaultSizing.FLOOR.type)
    }
}