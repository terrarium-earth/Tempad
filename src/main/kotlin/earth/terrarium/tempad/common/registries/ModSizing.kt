package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.sizing.DynamicAngledPlacement
import earth.terrarium.tempad.api.sizing.FloorPlacementSettings
import earth.terrarium.tempad.api.sizing.SizingRegistry
import earth.terrarium.tempad.api.sizing.VerticalPlacementSettings

object ModSizing {
    fun init() {
        SizingRegistry.register(DynamicAngledPlacement.type)
        SizingRegistry.register(FloorPlacementSettings.type)
        SizingRegistry.register(VerticalPlacementSettings.type)
    }
}