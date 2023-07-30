package me.codexadrian.tempad.common.network.messages;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record DeleteLocationPacket(UUID location) implements Packet<DeleteLocationPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "delete_location");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<DeleteLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<DeleteLocationPacket> {

        @Override
        public void encode(DeleteLocationPacket message, FriendlyByteBuf buffer) {
            buffer.writeUUID(message.location);
        }

        @Override
        public DeleteLocationPacket decode(FriendlyByteBuf buffer) {
            return new DeleteLocationPacket(buffer.readUUID());
        }

        @Override
        public PacketContext handle(DeleteLocationPacket message) {
            return (player, level) -> TempadLocationHandler.removeLocation(level, player.getUUID(), message.location);
        }
    }
}
