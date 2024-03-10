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

import java.util.function.Consumer;

public record OpenFavoritedLocationPacket(int color) implements Packet<OpenFavoritedLocationPacket> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<OpenFavoritedLocationPacket> type() {
        return HANDLER;
    }

    public static class Handler implements ServerboundPacketType<OpenFavoritedLocationPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "shortcut_favorited");

        @Override
        public Class<OpenFavoritedLocationPacket> type() {
            return OpenFavoritedLocationPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public OpenFavoritedLocationPacket decode(FriendlyByteBuf buffer) {
            return new OpenFavoritedLocationPacket(buffer.readVarInt());
        }

        @Override
        public void encode(OpenFavoritedLocationPacket packet, FriendlyByteBuf buffer) {
            buffer.writeVarInt(packet.color);
        }

        @Override
        public Consumer<Player> handle(OpenFavoritedLocationPacket message) {
            return player -> {
                ItemStack tempadStack = TeleportUtils.findTempad(player);
                LocationData locationData = LocationApi.API.get(player.level(), player.getUUID(), LocationApi.API.getFavorite(player.level(), player.getUUID()));
                if (locationData != null && tempadStack.getItem() instanceof TempadItem tempadItem && tempadItem.getOption().canTimedoorOpen(player, tempadStack) && TeleportUtils.mayTeleport(locationData.getLevelKey(), player)) {
                    if (!player.getAbilities().instabuild) tempadItem.getOption().onTimedoorOpen(player, tempadStack);
                    TempadItem.summonTimeDoor(locationData, player, message.color);
                }
            };
        }
    }
}