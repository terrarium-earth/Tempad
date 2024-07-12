package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.fuel.FuelHandler
import earth.terrarium.tempad.api.fuel.MutableFuelHandler
import earth.terrarium.tempad.common.apps.*
import earth.terrarium.tempad.common.utils.get
import net.minecraft.world.level.block.Blocks

object ModApps {
    val teleport = "teleport".tempadId
    val settings = "settings".tempadId
    val newLocation = "new_location".tempadId
    val fuel = "fuel".tempadId
    val timeline = "timeline".tempadId

    fun init() {
        AppRegistry[teleport] = ::TeleportApp
        AppRegistry[newLocation] = ::NewLocationApp
        AppRegistry[timeline] = ::TimelineApp
        AppRegistry[fuel] = { ctx ->
            val fuelType = ctx.stack[FuelHandler.CAPABILITY]
            if (fuelType is MutableFuelHandler) {
                FuelApp(ctx)
            } else {
                null
            }
        }

        AppRegistry[settings] = ::SettingsApp
    }
}