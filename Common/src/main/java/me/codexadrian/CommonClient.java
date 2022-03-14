package me.codexadrian;

import me.codexadrian.tempad.ColorConfig;
import me.codexadrian.tempad.client.gui.TempadScreen;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

import java.io.IOException;

public class CommonClient {
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
        int color = CommonClient.getClientConfig().getColor();
        Minecraft.getInstance().setScreen(new TempadScreen(color, interactionHand));
    }
}
