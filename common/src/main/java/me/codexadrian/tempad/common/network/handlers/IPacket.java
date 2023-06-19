package me.codexadrian.tempad.common.network.handlers;

import net.minecraft.resources.ResourceLocation;

public interface IPacket<T> {
    ResourceLocation getID();
    IPacketHandler<T> getHandler();
}
