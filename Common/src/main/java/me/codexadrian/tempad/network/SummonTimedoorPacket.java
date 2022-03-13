package me.codexadrian.tempad.network;

import me.codexadrian.tempad.network.handlers.IMessageHandler;
import me.codexadrian.tempad.network.handlers.IPacket;
import me.codexadrian.tempad.tempad.LocationData;
import me.codexadrian.tempad.tempad.TempadItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import static me.codexadrian.tempad.Constants.MODID;

public record SummonTimedoorPacket(ResourceLocation dimensionKey, BlockPos pos, InteractionHand hand) implements IPacket<SummonTimedoorPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(MODID, "timedoor");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IMessageHandler<SummonTimedoorPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IMessageHandler<SummonTimedoorPacket> {

        @Override
        public void encode(SummonTimedoorPacket message, FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(message.dimensionKey);
            buffer.writeBlockPos(message.pos);
            buffer.writeEnum(message.hand);
        }

        @Override
        public SummonTimedoorPacket decode(FriendlyByteBuf buffer) {
            return new SummonTimedoorPacket(buffer.readResourceLocation(), buffer.readBlockPos(), buffer.readEnum(InteractionHand.class));
        }

        @Override
        public boolean handle(SummonTimedoorPacket message, MinecraftServer server, Player player) {
            server.execute(() -> TempadItem.summonTimeDoor(new LocationData("", ResourceKey.create(Registry.DIMENSION_REGISTRY, message.dimensionKey), message.pos), player));
            return true;
        }
    }
}
