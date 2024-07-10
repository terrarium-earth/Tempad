package earth.terrarium.tempad.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.screen.*
import earth.terrarium.tempad.common.registries.ModMenus
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

@EventBusSubscriber(modid = Tempad.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object TempadClient {

    @SubscribeEvent @JvmStatic
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenus.TELEPORT_MENU, ::TeleportScreen)
        event.register(ModMenus.NEW_LOCATION_MENU, ::NewLocationScreen)
        event.register(ModMenus.SETTINGS_MENU, ::SettingsScreen)
        event.register(ModMenus.FUEL_MENU, ::FuelScreen)
        event.register(ModMenus.TIMELINE_MENU, ::TimelineScreen)
    }
}