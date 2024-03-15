package me.codexadrian.tempad.client.apps.impl;

import me.codexadrian.tempad.api.apps.TempadApp;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.c2s.RequestTeleportScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class TeleportApp implements TempadApp {
    public static final TeleportApp INSTANCE = new TeleportApp();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "teleport");
    public static final WidgetSprites WIDGET_SPRITES = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "apps/teleport/normal"),
        new ResourceLocation(Tempad.MODID, "apps/teleport/disabled"),
        new ResourceLocation(Tempad.MODID, "apps/teleport/hover")
    );

    @Override
    public WidgetSprites getWidgetSprites() {
        return WIDGET_SPRITES;
    }

    @Override
    public void open() {
        NetworkHandler.CHANNEL.sendToServer(new RequestTeleportScreen());
    }

    @Override
    public int priority() {
        return -2;
    }
}
