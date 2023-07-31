package me.codexadrian.tempad.common.utils;

import me.codexadrian.tempad.client.TimedoorErrorToast;
import me.codexadrian.tempad.client.gui.TempadScreen;
import me.codexadrian.tempad.common.network.messages.OpenTempadScreenPacket;
import me.codexadrian.tempad.common.network.messages.TimedoorErrorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientUtils {
    public static void openScreen(OpenTempadScreenPacket packet) {
        Minecraft.getInstance().setScreen(new TempadScreen(packet.hand(), packet.locationData()));
    }

    public static void showError(TimedoorErrorPacket packet) {
        if (Minecraft.getInstance().screen != null) {
            Minecraft.getInstance().getToasts().addToast(new TimedoorErrorToast(Component.nullToEmpty(packet.error())));
        }
    }
}
