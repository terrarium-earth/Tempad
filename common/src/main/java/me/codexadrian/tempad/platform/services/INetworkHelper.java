package me.codexadrian.tempad.platform.services;

import me.codexadrian.tempad.network.handlers.IPacketHandler;
import me.codexadrian.tempad.network.handlers.IPacket;
import net.minecraft.resources.ResourceLocation;

public interface INetworkHelper {

    <T extends IPacket<T>> void sendToServer(T packet);

    <T> void registerClientToServerPacket(ResourceLocation location, IPacketHandler<T> handler, Class<T> tClass);
}
