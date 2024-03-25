package me.codexadrian.tempad.client.apps.impl;

import me.codexadrian.tempad.api.apps.TempadApp;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.c2s.RequestAppScreen;
import me.codexadrian.tempad.common.network.messages.s2c.OpenTempadScreenPacket;
import me.codexadrian.tempad.common.utils.LookupLocation;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

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
    public void openOnClient(Player player, LookupLocation lookup) {
        NetworkHandler.CHANNEL.sendToServer(new RequestAppScreen(ID, lookup));
    }

    @Override
    public void openOnClient(Player player) {
        NetworkHandler.CHANNEL.sendToServer(new RequestAppScreen(ID));
    }

    @Override
    public void openOnServer(Player player, LookupLocation lookup) {
        OpenTempadScreenPacket packet = new OpenTempadScreenPacket(LocationApi.API.getAllAsList(player.level(), player.getUUID()), LocationApi.API.getFavorite(player.level(), player.getUUID()), lookup);
        NetworkHandler.CHANNEL.sendToPlayer(packet, player);
    }
}
