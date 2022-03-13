package me.codexadrian.tempad.network.handlers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public interface IMessageHandler<T> {

    void encode(T message, FriendlyByteBuf buffer);

    T decode(FriendlyByteBuf buffer);

    boolean handle(T message, MinecraftServer server, Player player);
}
