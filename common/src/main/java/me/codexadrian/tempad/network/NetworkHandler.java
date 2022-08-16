package me.codexadrian.tempad.network;

import me.codexadrian.tempad.network.messages.AddLocationPacket;
import me.codexadrian.tempad.network.messages.DeleteLocationPacket;
import me.codexadrian.tempad.network.messages.SummonBlockTimedoorPacket;
import me.codexadrian.tempad.network.messages.SummonTimedoorPacket;
import me.codexadrian.tempad.platform.Services;

public class NetworkHandler {
    public static void register() {
        Services.NETWORK.registerClientToServerPacket(SummonTimedoorPacket.ID, SummonTimedoorPacket.HANDLER, SummonTimedoorPacket.class);
        Services.NETWORK.registerClientToServerPacket(SummonBlockTimedoorPacket.ID, SummonBlockTimedoorPacket.HANDLER, SummonBlockTimedoorPacket.class);
        Services.NETWORK.registerClientToServerPacket(AddLocationPacket.ID, AddLocationPacket.HANDLER, AddLocationPacket.class);
        Services.NETWORK.registerClientToServerPacket(DeleteLocationPacket.ID, DeleteLocationPacket.HANDLER, DeleteLocationPacket.class);
    }
}
