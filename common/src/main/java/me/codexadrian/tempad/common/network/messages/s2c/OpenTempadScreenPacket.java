package me.codexadrian.tempad.common.network.messages.s2c;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public record OpenTempadScreenPacket(List<LocationData> locationData,
                                     Optional<UUID> favorite) implements Packet<OpenTempadScreenPacket> {
    public static final Handler HANDLER = new Handler();

    public OpenTempadScreenPacket(List<LocationData> locationData, @Nullable UUID favorite) {
        this(locationData, Optional.ofNullable(favorite));
    }

    @Override
    public PacketType<OpenTempadScreenPacket> type() {
        return HANDLER;
    }

    public static class Handler extends CodecPacketType<OpenTempadScreenPacket> implements ClientboundPacketType<OpenTempadScreenPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "open_screen");
        public static final ByteCodec<OpenTempadScreenPacket> CODEC = ObjectByteCodec.create(
                LocationData.CODEC.listOf().fieldOf(OpenTempadScreenPacket::locationData),
                ByteCodec.UUID.optionalFieldOf(OpenTempadScreenPacket::favorite),
                OpenTempadScreenPacket::new
        );

        public Handler() {
            super(OpenTempadScreenPacket.class, ID, CODEC);
        }

        @Override
        public Runnable handle(OpenTempadScreenPacket message) {
            return () -> ClientUtils.openScreen(message);
        }
    }
}
