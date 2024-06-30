package earth.terrarium.tempad.client

import earth.terrarium.tempad.client.screen.TeleportScreen
import earth.terrarium.tempad.common.registries.ModMenus
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

@Mod("tempad", dist = [Dist.CLIENT])
class TempadClient {
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenus.TELEPORT_MENU, ::TeleportScreen)
    }
}