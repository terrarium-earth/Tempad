package earth.terrarium.tempad.common.utils;

import earth.terrarium.tempad.api.apps.TempadAppApi;
import earth.terrarium.tempad.client.config.TempadClientConfig;
import earth.terrarium.tempad.client.screens.TeleportScreen;
import earth.terrarium.tempad.common.network.NetworkHandler;
import earth.terrarium.tempad.common.network.messages.c2s.OpenFavoritedLocationPacket;
import earth.terrarium.tempad.common.network.messages.c2s.RequestAppScreen;
import earth.terrarium.tempad.common.network.messages.s2c.OpenTempadScreenPacket;
import net.minecraft.client.Minecraft;

import java.util.Optional;

public class ClientUtils {
    public static void openScreen(OpenTempadScreenPacket packet) {
        Minecraft.getInstance().setScreen(new TeleportScreen(packet.locationData(), packet.favorite().orElse(null), packet.lookup()));
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

    public static void openTempadbyShortcut() {
        TempadAppApi.API.getHomePage().openOnClient(Minecraft.getInstance().player);
    }

    public static void openFavorited(LookupLocation lookup) {
        OpenFavoritedLocationPacket packet = new OpenFavoritedLocationPacket(TempadClientConfig.color, Optional.ofNullable(lookup));
        NetworkHandler.CHANNEL.sendToServer(packet);
    }

    public static void openFavorited() {
        openFavorited(null);
    }
}
