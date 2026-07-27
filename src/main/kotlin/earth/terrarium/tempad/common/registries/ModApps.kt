package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.app.TempadAppRegistry
import earth.terrarium.tempad.common.apps.*

object ModApps {
    val teleport = "teleport".tempadId
    val portalSetup = "portal_setup".tempadId
    val newLocation = "new_location".tempadId
    val timeline = "timeline".tempadId
    val settings = "settings".tempadId
    val guide = ModItems.guideKey

    fun init() {
        TempadAppRegistry[teleport] = ::TeleportApp
        // TempadAppRegistry[portalSetup] = { ctx -> if(isStationary) PortalSetupApp(ctx) else null }
        TempadAppRegistry[newLocation] = ::NewLocationApp
        TempadAppRegistry[timeline] = ::TimelineApp
        TempadAppRegistry[settings] = ::SettingsApp
        TempadAppRegistry[guide] = ::KnowledgeRepositoryApp
    }
}