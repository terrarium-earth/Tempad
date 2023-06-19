package me.codexadrian.tempad.common.network.messages;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.TempadComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import java.util.UUID;

public record DeleteLocationPacket(UUID location, InteractionHand hand) implements Packet<DeleteLocationPacket> {
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
            buffer.writeEnum(message.hand);
        }

        @Override
        public DeleteLocationPacket decode(FriendlyByteBuf buffer) {
            return new DeleteLocationPacket(buffer.readUUID(), buffer.readEnum(InteractionHand.class));
        }

        @Override
        public PacketContext handle(DeleteLocationPacket message) {
            return (player, level) -> TempadComponent.deleteStackLocation(player.getItemInHand(message.hand), message.location);
        }
    }
}
