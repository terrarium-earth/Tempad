package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.s2c.OpenTempadScreenPacket;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record RequestTeleportScreen() implements Packet<RequestTeleportScreen> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<RequestTeleportScreen> type() {
        return HANDLER;
    }

    public static class Handler implements ServerboundPacketType<RequestTeleportScreen> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "teleport_screen");

        @Override
        public Class<RequestTeleportScreen> type() {
            return RequestTeleportScreen.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public RequestTeleportScreen decode(FriendlyByteBuf buffer) {
            return new RequestTeleportScreen();
        }

        @Override
        public void encode(RequestTeleportScreen packet, FriendlyByteBuf buffer) {}

        @Override
        public Consumer<Player> handle(RequestTeleportScreen message) {
            return player -> {
                if (!TeleportUtils.hasTempad(player))
                    return;

                OpenTempadScreenPacket packet = new OpenTempadScreenPacket(LocationApi.API.getAllAsList(player.level(), player.getUUID()), LocationApi.API.getFavorite(player.level(), player.getUUID()));
                NetworkHandler.CHANNEL.sendToPlayer(packet, player);
            };
        }
    }
}