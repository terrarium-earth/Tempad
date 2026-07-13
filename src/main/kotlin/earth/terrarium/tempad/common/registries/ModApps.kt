package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.common.apps.*

object ModApps {
    val teleport = "teleport".tempadId
    val portalSetup = "portal_setup".tempadId
    val newLocation = "new_location".tempadId
    val timeline = "timeline".tempadId
    val settings = "settings".tempadId
    val guide = ModItems.guideKey

    fun init() {
        AppRegistry[teleport] = { ctx, isStationary -> if(!isStationary) TeleportApp(ctx) else null}
        AppRegistry[portalSetup] = { ctx, isStationary -> if(isStationary) PortalSetupApp(ctx) else null }
        AppRegistry[newLocation] = ::NewLocationApp
        AppRegistry[timeline] = ::TimelineApp
        AppRegistry[settings] = ::SettingsApp
        AppRegistry[guide] = ::KnowledgeRepositoryApp
    }
}