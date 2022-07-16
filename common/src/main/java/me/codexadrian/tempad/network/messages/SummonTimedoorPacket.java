package me.codexadrian.tempad.network.messages;

import me.codexadrian.tempad.network.handlers.IPacketHandler;
import me.codexadrian.tempad.network.handlers.IPacket;
import me.codexadrian.tempad.data.LocationData;
import me.codexadrian.tempad.tempad.TempadItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

import static me.codexadrian.tempad.Constants.MODID;

public record SummonTimedoorPacket(ResourceLocation dimensionKey, BlockPos pos, InteractionHand hand, int color) implements IPacket<SummonTimedoorPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(MODID, "timedoor");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<SummonTimedoorPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<SummonTimedoorPacket> {

        @Override
        public void encode(SummonTimedoorPacket message, FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(message.dimensionKey);
            buffer.writeBlockPos(message.pos);
            buffer.writeEnum(message.hand);
            buffer.writeVarInt(message.color);
        }

        @Override
        public SummonTimedoorPacket decode(FriendlyByteBuf buffer) {
            return new SummonTimedoorPacket(buffer.readResourceLocation(), buffer.readBlockPos(), buffer.readEnum(InteractionHand.class), buffer.readVarInt());
        }

        @Override
        public BiConsumer<MinecraftServer, Player> handle(SummonTimedoorPacket message) {
            return (server, player) -> {
                ItemStack itemInHand = player.getItemInHand(message.hand());
                if(itemInHand.getItem() instanceof TempadItem tempadItem && !player.getAbilities().instabuild) {
                    tempadItem.getOption().onTimedoorOpen(player, message.hand());
                }
                TempadItem.summonTimeDoor(new LocationData("", ResourceKey.create(Registry.DIMENSION_REGISTRY, message.dimensionKey), message.pos), player, message.color);
            };
        }
    }
}
