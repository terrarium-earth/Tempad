package me.codexadrian.tempad.common.network.messages.s2c;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.utils.ClientUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public record OpenTempadScreenPacket(List<LocationData> locationData,
                                     UUID favorite) implements Packet<OpenTempadScreenPacket> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<OpenTempadScreenPacket> type() {
        return HANDLER;
    }

    public static class Handler implements ClientboundPacketType<OpenTempadScreenPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "open_screen");

        @Override
        public Class<OpenTempadScreenPacket> type() {
            return OpenTempadScreenPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public void encode(OpenTempadScreenPacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(message.locationData, (buf, locationData) -> {
                buf.writeUUID(locationData.getId());
                buf.writeUtf(locationData.getName());
                buf.writeBlockPos(locationData.getBlockPos());
                buf.writeResourceKey(locationData.getLevelKey());
                buf.writeBoolean(locationData.isTeleportable());
                buf.writeBoolean(locationData.isDeletable());
                buf.writeBoolean(locationData.isDownloadable());
            });
            buffer.writeOptional(Optional.ofNullable(message.favorite), FriendlyByteBuf::writeUUID);
        }

        @Override
        public OpenTempadScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenTempadScreenPacket(buffer.readList((buf) -> {
                UUID id = buf.readUUID();
                String name = buf.readUtf();
                BlockPos pos = buf.readBlockPos();
                ResourceKey<Level> levelResourceKey = buf.readResourceKey(Registries.DIMENSION);
                boolean teleportable = buf.readBoolean();
                boolean deletable = buf.readBoolean();
                boolean downloadable = buf.readBoolean();
                return new LocationData(name, levelResourceKey, pos, id, teleportable, deletable, downloadable);
            }), buffer.readOptional(FriendlyByteBuf::readUUID).orElse(null));
        }

        @Override
        public Runnable handle(OpenTempadScreenPacket message) {
            return () -> ClientUtils.openScreen(message);
        }
    }
}
