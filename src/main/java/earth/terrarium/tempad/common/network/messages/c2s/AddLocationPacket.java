package earth.terrarium.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.data.LocationData;
import earth.terrarium.tempad.common.utils.TeleportUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.Consumer;

public record AddLocationPacket(String name) implements Packet<AddLocationPacket> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<AddLocationPacket> type() {
        return HANDLER;
    }

    public static class Handler implements ServerboundPacketType<AddLocationPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "add_location");

        @Override
        public Class<AddLocationPacket> type() {
            return AddLocationPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public void encode(AddLocationPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.name);
        }

        @Override
        public AddLocationPacket decode(FriendlyByteBuf buffer) {
            return new AddLocationPacket(buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(AddLocationPacket message) {
            return (player) -> {
                if (!TeleportUtils.hasTempad(player)) return;
                var tempadLocation = new LocationData(message.name, player.level().dimension(), new BlockPos(player.getBlockX(), Math.round((float) player.getY()), player.getBlockZ()), player.getYHeadRot());
                LocationApi.API.add(player.level(), player.getUUID(), tempadLocation);
            };
        }
    }
}
