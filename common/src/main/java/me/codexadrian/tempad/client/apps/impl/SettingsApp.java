package me.codexadrian.tempad.client.apps.impl;

import me.codexadrian.tempad.api.apps.TempadApp;
import me.codexadrian.tempad.client.screens.TempadScreen;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.utils.LookupLocation;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class SettingsApp implements TempadApp {
    public static final SettingsApp INSTANCE = new SettingsApp();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "settings");
    public static final WidgetSprites WIDGET_SPRITES = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "apps/settings/normal"),
        new ResourceLocation(Tempad.MODID, "apps/settings/disabled"),
        new ResourceLocation(Tempad.MODID, "apps/settings/hover")
    );

    @Override
    public WidgetSprites getWidgetSprites() {
        return WIDGET_SPRITES;
    }

    @Override
    public void openOnClient(Player player, LookupLocation lookup) {

    }

    @Override
    public void openOnServer(Player player, LookupLocation lookup) {

    }
}
