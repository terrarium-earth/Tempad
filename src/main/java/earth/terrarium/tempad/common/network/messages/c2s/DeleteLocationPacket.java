package earth.terrarium.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.utils.TeleportUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.Consumer;

public record DeleteLocationPacket(UUID location) implements Packet<DeleteLocationPacket> {

    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<DeleteLocationPacket> type() {
        return HANDLER;
    }

    public static class Handler implements ServerboundPacketType<DeleteLocationPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "delete_location");

        @Override
        public Class<DeleteLocationPacket> type() {
            return DeleteLocationPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public DeleteLocationPacket decode(FriendlyByteBuf buf) {
            return new DeleteLocationPacket(buf.readUUID());
        }

        @Override
        public void encode(DeleteLocationPacket packet, FriendlyByteBuf buffer) {
            buffer.writeUUID(packet.location);
        }

        @Override
        public Consumer<Player> handle(DeleteLocationPacket message) {
            return player -> {
                if (TeleportUtils.hasTempad(player)) {
                    LocationApi.API.remove(player.level(), player.getUUID(), message.location);
                }
            };
        }
    }
}