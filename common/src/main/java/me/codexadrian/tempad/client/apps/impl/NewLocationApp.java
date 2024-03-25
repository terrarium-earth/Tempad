package me.codexadrian.tempad.client.apps.impl;

import me.codexadrian.tempad.api.apps.TempadApp;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.client.screens.NewLocationScreen;
import me.codexadrian.tempad.client.screens.TempadScreen;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.s2c.OpenTempadScreenPacket;
import me.codexadrian.tempad.common.utils.LookupLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

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
    public void openOnClient(Player player, LookupLocation lookup) {
        Minecraft.getInstance().setScreen(new NewLocationScreen(lookup));
    }

    @Override
    public void openOnServer(Player player, LookupLocation lookup) {
        NetworkHandler.CHANNEL.sendToPlayer(new OpenTempadScreenPacket(LocationApi.API.getAllAsList(player.level(), player.getUUID()), LocationApi.API.getFavorite(player.level(), player.getUUID()), lookup), player);
    }

    @Override
    public int priority() {
        return -1;
    }
}
