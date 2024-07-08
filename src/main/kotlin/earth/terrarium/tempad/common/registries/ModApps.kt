package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.fuel.FuelHandler
import earth.terrarium.tempad.api.fuel.MutableFuelHandler
import earth.terrarium.tempad.common.apps.FuelApp
import earth.terrarium.tempad.common.apps.NewLocationApp
import earth.terrarium.tempad.common.apps.SettingsApp
import earth.terrarium.tempad.common.apps.TeleportApp
import earth.terrarium.tempad.common.utils.get

object ModApps {
    val TELEPORT = "teleport".tempadId
    val SETTINGS = "settings".tempadId
    val NEW_LOCATION = "new_location".tempadId
    val FUEL = "fuel".tempadId

    fun init() {
        AppRegistry[TELEPORT] = { _, id -> TeleportApp(id) }
        AppRegistry[NEW_LOCATION] = { _, id -> NewLocationApp(id) }
        AppRegistry[FUEL] = { player, slotId ->
            val fuelType = player.inventory[slotId][FuelHandler.CAPABILITY]
            if (fuelType is MutableFuelHandler) {
                FuelApp(slotId)
            } else {
                null
            }
        }
        AppRegistry[SETTINGS] = { _, id -> SettingsApp(id) }
    }
}