package me.codexadrian.tempad.network.messages;

import me.codexadrian.tempad.network.handlers.IPacketHandler;
import me.codexadrian.tempad.network.handlers.IPacket;
import me.codexadrian.tempad.tempad.TempadComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.BiConsumer;

import static me.codexadrian.tempad.Constants.MODID;

public record DeleteLocationPacket(UUID location, InteractionHand hand) implements IPacket<DeleteLocationPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(MODID, "delete_location");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<DeleteLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<DeleteLocationPacket> {

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
        public BiConsumer<MinecraftServer, Player> handle(DeleteLocationPacket message) {
            return (server, player) -> TempadComponent.deleteStackLocation(player.getItemInHand(message.hand), message.location);
        }
    }
}
