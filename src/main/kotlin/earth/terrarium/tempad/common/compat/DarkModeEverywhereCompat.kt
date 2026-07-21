package earth.terrarium.tempad.common.compat

import earth.terrarium.tempad.Tempad
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.InterModComms
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(modid = Tempad.MOD_ID, value = [Dist.CLIENT])
object DarkModeEverywhereCompat {
    @SubscribeEvent
    fun sendIMC(event: FMLClientSetupEvent) {
        val blacklist = listOf(
            { "tempad" },
            { "olympus" }
        )

        for (screen in blacklist) {
            InterModComms.sendTo("darkmodeeverywhere", "dme-shaderblacklist", screen)
        }
    }
}