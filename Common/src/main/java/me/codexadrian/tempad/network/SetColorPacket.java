package me.codexadrian.tempad.network;

import me.codexadrian.tempad.network.handlers.IMessageHandler;
import me.codexadrian.tempad.network.handlers.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import static me.codexadrian.tempad.Constants.MODID;

public record SetColorPacket(int color) implements IPacket<SetColorPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(MODID, "color");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IMessageHandler<SetColorPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IMessageHandler<SetColorPacket> {

        @Override
        public void encode(SetColorPacket message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.color);
        }

        @Override
        public SetColorPacket decode(FriendlyByteBuf buffer) {
            return new SetColorPacket(buffer.readVarInt());
        }

        @Override
        public boolean handle(SetColorPacket message, MinecraftServer server, Player player) {
            /* TODO fix this shit
            server.execute(() -> {
                ColorDataComponent.COLOR_DATA.get(player).setColor(color);
                ColorDataComponent.COLOR_DATA.sync(player);
            });
             */
            return true;
        }
    }
}
