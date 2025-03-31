package earth.terrarium.tempad.client

import com.mojang.blaze3d.platform.InputConstants
import earth.terrarium.argonauts.client.utils.ClientUtils
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.network.c2s.OpenAppPacket
import earth.terrarium.tempad.common.network.c2s.OpenTempadPacket
import earth.terrarium.tempad.common.network.c2s.UseMacroPacket
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.utils.sendToServer
import net.minecraft.client.KeyMapping
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.common.NeoForge


@EventBusSubscriber(value = [Dist.CLIENT], bus = EventBusSubscriber.Bus.MOD, modid = Tempad.MOD_ID)
object ModKeybinds {
    val openDefaultApp: KeyMapping = KeyMapping(
        "key.tempad.shortcut",  // The translation key of the keybinding's name
        InputConstants.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        InputConstants.UNKNOWN.value,  // The keycode of the key
        "category.tempad" // The translation key of the keybinding's category.
    )

    val useMacro: KeyMapping = KeyMapping(
        "key.tempad.macro",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.value,
        "category.tempad"
    )

    val newLocation: KeyMapping = KeyMapping(
        "key.tempad.new_location",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.value,
        "category.tempad"
    )

    val travelTimeline: KeyMapping = KeyMapping(
        "key.tempad.travel_timeline",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.value,
        "category.tempad"
    )

    @SubscribeEvent @JvmStatic
    fun init(event: FMLClientSetupEvent) {
        NeoForge.EVENT_BUS.addListener(::onClientTick);
    }

    @SubscribeEvent @JvmStatic
    fun registerKeyBinding(event: RegisterKeyMappingsEvent) {
        event.register(openDefaultApp)
        event.register(useMacro)
        event.register(newLocation)
        event.register(travelTimeline)
    }

    private fun onClientTick(event: ClientTickEvent.Post) {
        while (openDefaultApp.consumeClick()) {
            OpenTempadPacket().sendToServer()
        }
        while (useMacro.consumeClick()) {
            UseMacroPacket().sendToServer()
        }
        while (newLocation.consumeClick()) {
            OpenAppPacket(ModApps.newLocation).sendToServer()
        }
        while (travelTimeline.consumeClick()) {
            OpenAppPacket(ModApps.timeline).sendToServer()
        }
    }
}