package me.codexadrian.tempad.client.apps.impl;

import me.codexadrian.tempad.api.apps.TempadApp;
import me.codexadrian.tempad.client.screens.NewLocationScreen;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NewLocationApp implements TempadApp {
    public static final NewLocationApp INSTANCE = new NewLocationApp();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "new_location");
    public static final WidgetSprites WIDGET_SPRITES = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "apps/new_location/normal"),
        new ResourceLocation(Tempad.MODID, "apps/new_location/disabled"),
        new ResourceLocation(Tempad.MODID, "apps/new_location/hover")
    );

    @Override
    public WidgetSprites getWidgetSprites() {
        return WIDGET_SPRITES;
    }

    @Override
    public void open() {
        Minecraft.getInstance().setScreen(new NewLocationScreen());
    }

    @Override
    public int priority() {
        return -1;
    }
}
