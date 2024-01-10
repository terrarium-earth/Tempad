package me.codexadrian.tempad.common.network;

import com.teamresourceful.resourcefullib.common.network.Network;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.messages.c2s.*;
import me.codexadrian.tempad.common.network.messages.s2c.InitConfigPacket;
import me.codexadrian.tempad.common.network.messages.s2c.OpenTempadScreenPacket;
import net.minecraft.resources.ResourceLocation;


public class NetworkHandler {
    public static final Network CHANNEL = new Network(new ResourceLocation(Tempad.MODID, "main"), 0, false);

    public static void register() {
        CHANNEL.register(AddLocationPacket.HANDLER);
        CHANNEL.register(DeleteLocationPacket.HANDLER);
        CHANNEL.register(ExportLocationPacket.HANDLER);
        CHANNEL.register(FavoriteLocationPacket.HANDLER);
        CHANNEL.register(OpenFavoritedLocationPacket.HANDLER);
        CHANNEL.register(OpenTempadByShortcutPacket.HANDLER);
        CHANNEL.register(SummonTimedoorPacket.HANDLER);

        CHANNEL.register(InitConfigPacket.HANDLER);
        CHANNEL.register(OpenTempadScreenPacket.HANDLER);
    }
}
