package earth.terrarium.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.common.Tempad;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public record FavoriteLocationPacket(@Nullable UUID location) implements Packet<FavoriteLocationPacket> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<FavoriteLocationPacket> type() {
        return HANDLER;
    }

    public static class Handler implements ServerboundPacketType<FavoriteLocationPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "favorite_location");

        @Override
        public Class<FavoriteLocationPacket> type() {
            return FavoriteLocationPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public FavoriteLocationPacket decode(FriendlyByteBuf buffer) {
            return new FavoriteLocationPacket(buffer.readOptional(FriendlyByteBuf::readUUID).orElse(null));
        }

        @Override
        public void encode(FavoriteLocationPacket packet, FriendlyByteBuf buffer) {
            buffer.writeOptional(Optional.ofNullable(packet.location), FriendlyByteBuf::writeUUID);
        }

        @Override
        public Consumer<Player> handle(FavoriteLocationPacket message) {
            return player -> {
                LocationApi.API.getFavorite(player.level(), player.getUUID());
                if (message.location != null) {
                    LocationApi.API.favorite(player.level(), player.getUUID(), message.location);
                } else {
                    LocationApi.API.unfavorite(player.level(), player.getUUID());
                }
            };
        }
    }
}