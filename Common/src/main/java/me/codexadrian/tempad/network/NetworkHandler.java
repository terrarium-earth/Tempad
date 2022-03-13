package me.codexadrian.tempad.network;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.resources.ResourceLocation;

public class NetworkHandler {
    public static void register() {
        Services.NETWORK.registerClientToServerPacket(SummonTimedoorPacket.ID, SummonTimedoorPacket.HANDLER, SummonTimedoorPacket.class);
        Services.NETWORK.registerClientToServerPacket(AddLocationPacket.ID, AddLocationPacket.HANDLER, AddLocationPacket.class);
        Services.NETWORK.registerClientToServerPacket(SetColorPacket.ID, SetColorPacket.HANDLER, SetColorPacket.class);
        Services.NETWORK.registerClientToServerPacket(DeleteLocationPacket.ID, DeleteLocationPacket.HANDLER, DeleteLocationPacket.class);
    }
}
