package me.codexadrian.tempad.common.forge;

import com.mojang.blaze3d.platform.InputConstants;
import me.codexadrian.tempad.common.utils.ClientUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    private static final KeyMapping SHORTCUT_KEYBIND = new KeyMapping(
        "key.tempad.shortcut", // The translation key of the keybinding's name
        InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        GLFW.GLFW_KEY_V, // The keycode of the key
        "category.tempad.keybinds" // The translation key of the keybinding's category.
    );

    private static final KeyMapping FAVORITED_KEYBIND = new KeyMapping(
        "key.tempad.favorite", // The translation key of the keybinding's name
        InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        GLFW.GLFW_KEY_G, // The keycode of the key
        "category.tempad.keybinds" // The translation key of the keybinding's category.
    );

    public static void registerKeyBinding(RegisterKeyMappingsEvent event) {
        event.register(SHORTCUT_KEYBIND);
        event.register(FAVORITED_KEYBIND);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            while (SHORTCUT_KEYBIND.consumeClick()) {
                ClientUtils.openTempadbyShortcut();
            }
            while (FAVORITED_KEYBIND.consumeClick()) {
                ClientUtils.openFavorited();
            }
        }
    }
}