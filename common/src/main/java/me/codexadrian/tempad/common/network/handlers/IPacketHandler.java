package me.codexadrian.tempad.common.network.handlers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface IPacketHandler<T> {

    void encode(T message, FriendlyByteBuf buffer);

    T decode(FriendlyByteBuf buffer);

    //boolean handle(T message, MinecraftServer server, Player player);

    BiConsumer<MinecraftServer, Player> handle(T message);
}
