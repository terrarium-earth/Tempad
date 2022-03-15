package me.codexadrian.tempad;

import me.codexadrian.tempad.client.gui.TempadScreen;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

import java.io.IOException;

public class TempadClient {
    private static ColorConfig clientConfig;
    public static void init() {
        try {
            clientConfig = ColorConfig.loadConfig(Services.PLATFORM.getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ColorConfig getClientConfig() {
        return clientConfig;
    }

    public static void openScreen(InteractionHand interactionHand) {
        //int color = ColorDataComponent.COLOR_DATA.get(player).getColor();
        int color = TempadClient.getClientConfig().getColor();
        Minecraft.getInstance().setScreen(new TempadScreen(color, interactionHand));
    }
}
