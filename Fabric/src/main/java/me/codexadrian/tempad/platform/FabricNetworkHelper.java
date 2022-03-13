package me.codexadrian.tempad.platform;

import io.netty.buffer.Unpooled;
import me.codexadrian.tempad.network.handlers.IMessageHandler;
import me.codexadrian.tempad.network.handlers.IPacket;
import me.codexadrian.tempad.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class FabricNetworkHelper implements INetworkHelper {

    public <T extends IPacket<T>> void sendToServer(T packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.getHandler().encode(packet, buf);
        ClientPlayNetworking.send(packet.getID(), buf);
    }

    @Override
    public <T> void registerClientToServerPacket(ResourceLocation location, IMessageHandler<T> handler, Class<T> tClass) {
        ServerPlayNetworking.registerGlobalReceiver(location, (server, player, handler1, buf, responseSender) -> handler.handle(handler.decode(buf), server, player));
    }
}
