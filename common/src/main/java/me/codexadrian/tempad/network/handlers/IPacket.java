package me.codexadrian.tempad.network.handlers;

import net.minecraft.resources.ResourceLocation;

public interface IPacket<T> {
    ResourceLocation getID();
    IPacketHandler<T> getHandler();
}
