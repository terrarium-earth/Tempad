package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.common.apps.NewLocationApp
import earth.terrarium.tempad.common.apps.SettingsApp
import earth.terrarium.tempad.common.apps.TeleportApp

object ModApps {
    val TELEPORT = "teleport".tempadId
    val SETTINGS = "settings".tempadId
    val NEW_LOCATION = "new_location".tempadId

    fun init() {
        AppRegistry.register(TELEPORT, ::TeleportApp)
        AppRegistry.register(SETTINGS, ::SettingsApp)
        AppRegistry.register(NEW_LOCATION, ::NewLocationApp)
    }
}