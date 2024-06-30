package earth.terrarium.tempad.client

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.screen.TeleportScreen
import earth.terrarium.tempad.common.registries.ModMenus
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

@Mod(Tempad.MOD_ID, dist = [Dist.CLIENT])
class TempadClient(bus: IEventBus) {
    init {
        bus.register(this::registerScreens)
    }

    private fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenus.TELEPORT_MENU, ::TeleportScreen)
    }
}