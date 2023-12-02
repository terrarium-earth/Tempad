package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record FavoriteLocationPacket(@Nullable UUID location) implements Packet<FavoriteLocationPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "favorite_location");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<FavoriteLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<FavoriteLocationPacket> {

        @Override
        public void encode(FavoriteLocationPacket message, FriendlyByteBuf buffer) {
            buffer.writeOptional(Optional.ofNullable(message.location), FriendlyByteBuf::writeUUID);
        }

        @Override
        public FavoriteLocationPacket decode(FriendlyByteBuf buffer) {
            return new FavoriteLocationPacket(buffer.readOptional(FriendlyByteBuf::readUUID).orElse(null));
        }

        @Override
        public PacketContext handle(FavoriteLocationPacket message) {
            return (player, level) -> {
                TempadLocationHandler.getFavorite(level, player.getUUID());
                if (message.location != null) {
                    TempadLocationHandler.favoriteLocation(level, player.getUUID(), message.location);
                } else {
                    TempadLocationHandler.unfavoriteLocation(level, player.getUUID());
                }
            };
        }
    }
}
