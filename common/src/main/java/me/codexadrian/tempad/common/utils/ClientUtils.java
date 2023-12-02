package me.codexadrian.tempad.common.utils;

import me.codexadrian.tempad.client.gui.ConsolidatedScreen;
import me.codexadrian.tempad.common.network.messages.s2c.OpenTempadScreenPacket;
import net.minecraft.client.Minecraft;

public class ClientUtils {
    public static void openScreen(OpenTempadScreenPacket packet) {
        Minecraft.getInstance().setScreen(new ConsolidatedScreen(packet.locationData(), packet.favorite()));
    }

    public static int darkenColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = (int) (r * 0.8);
        g = (int) (g * 0.8);
        b = (int) (b * 0.8);

        return (r << 16) | (g << 8) | b;
    }
}
