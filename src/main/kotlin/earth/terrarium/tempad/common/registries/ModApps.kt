package earth.terrarium.tempad.common.registries

import com.mojang.datafixers.kinds.App
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.tva_device.upgrades
import earth.terrarium.tempad.common.apps.*
import earth.terrarium.tempad.common.config.CommonConfig

object ModApps {
    val teleport = "teleport".tempadId
    val portalSetup = "portal_setup".tempadId
    val newLocation = "new_location".tempadId
    val timeline = "timeline".tempadId
    val settings = "settings".tempadId

    fun init() {
        AppRegistry[teleport] = { ctx, isStationary -> if(!isStationary) TeleportApp(ctx) else null}
        AppRegistry[portalSetup] = { ctx, isStationary -> if(isStationary) PortalSetupApp(ctx) else null }
        AppRegistry[newLocation] = register@{ ctx, isStationary ->
            val server = Tempad.server ?: return@register null
            if(CommonConfig.allowLocationSaving && !isStationary && (Tempad.flag !in server.overworld().enabledFeatures() || ctx.stack.upgrades?.contains(ModItems.newLocationKey) == true)) {
                NewLocationApp(ctx)
            } else {
                null
            }
        }
        AppRegistry[timeline] = { ctx, isStationary -> if(!ctx.stack.twisterEquipped) null else TimelineApp(ctx, isStationary) }
        AppRegistry[settings] = ::SettingsApp
    }
}