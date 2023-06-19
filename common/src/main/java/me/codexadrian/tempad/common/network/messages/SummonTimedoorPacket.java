package me.codexadrian.tempad.common.network.messages;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.tempad.TempadItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public record SummonTimedoorPacket(ResourceLocation dimensionKey, BlockPos pos, InteractionHand hand, int color) implements Packet<SummonTimedoorPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "timedoor");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SummonTimedoorPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<SummonTimedoorPacket> {

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
        public PacketContext handle(SummonTimedoorPacket message) {
            return (player, level) -> {
                ItemStack itemInHand = player.getItemInHand(message.hand());
                if(itemInHand.getItem() instanceof TempadItem tempadItem && !player.getAbilities().instabuild) {
                    tempadItem.getOption().onTimedoorOpen(player, message.hand());
                }
                TempadItem.summonTimeDoor(new LocationData("", ResourceKey.create(Registries.DIMENSION, message.dimensionKey), message.pos), player, message.color);
            };
        }
    }
}
