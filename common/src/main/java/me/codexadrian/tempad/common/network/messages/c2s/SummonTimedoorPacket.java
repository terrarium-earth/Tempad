package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public record SummonTimedoorPacket(UUID location, int color) implements Packet<SummonTimedoorPacket> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<SummonTimedoorPacket> type() {
        return HANDLER;
    }

    public static class Handler implements ServerboundPacketType<SummonTimedoorPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "timedoor");

        @Override
        public Class<SummonTimedoorPacket> type() {
            return SummonTimedoorPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public SummonTimedoorPacket decode(FriendlyByteBuf buffer) {
            return new SummonTimedoorPacket(buffer.readUUID(), buffer.readVarInt());
        }

        @Override
        public void encode(SummonTimedoorPacket packet, FriendlyByteBuf buffer) {
            buffer.writeUUID(packet.location);
            buffer.writeVarInt(packet.color);
        }

        @Override
        public Consumer<Player> handle(SummonTimedoorPacket message) {
            return player -> {
                ItemStack itemInHand = TeleportUtils.findTempad(player);
                LocationData locationData = LocationApi.API.get(player.level(), player.getUUID(), message.location);
                if (locationData != null && itemInHand.getItem() instanceof TempadItem tempadItem && tempadItem.getOption().canTimedoorOpen(player, itemInHand) && TeleportUtils.mayTeleport(locationData.getLevelKey(), player)) {
                    if (!player.getAbilities().instabuild) tempadItem.getOption().onTimedoorOpen(player, itemInHand);
                    TempadItem.summonTimeDoor(locationData, player, message.color);
                }
            };
        }
    }
}