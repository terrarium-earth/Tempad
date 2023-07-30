package me.codexadrian.tempad.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.messages.AddLocationPacket;
import me.codexadrian.tempad.common.network.messages.DeleteLocationPacket;
import me.codexadrian.tempad.common.network.messages.OpenTempadScreenPacket;
import me.codexadrian.tempad.common.network.messages.SummonTimedoorPacket;


public class NetworkHandler {
    public static final NetworkChannel CHANNEL = new NetworkChannel(Tempad.MODID, 0, "main");

    public static void register() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SummonTimedoorPacket.ID, SummonTimedoorPacket.HANDLER, SummonTimedoorPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, AddLocationPacket.ID, AddLocationPacket.HANDLER, AddLocationPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, DeleteLocationPacket.ID, DeleteLocationPacket.HANDLER, DeleteLocationPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenTempadScreenPacket.ID, OpenTempadScreenPacket.HANDLER, OpenTempadScreenPacket.class);
    }
}
