package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.common.apps.*

object ModApps {
    val teleport = "teleport".tempadId
    val settings = "settings".tempadId
    val newLocation = "new_location".tempadId
    val timeline = "timeline".tempadId

    fun init() {
        AppRegistry[teleport] = ::TeleportApp
        AppRegistry[newLocation] = ::NewLocationApp
        AppRegistry[timeline] = ::TimelineApp

        AppRegistry[settings] = ::SettingsApp
    }
}