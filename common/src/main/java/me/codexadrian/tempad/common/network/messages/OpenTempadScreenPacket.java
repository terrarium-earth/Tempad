package me.codexadrian.tempad.common.network.messages;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import me.codexadrian.tempad.client.gui.TempadScreen;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public record OpenTempadScreenPacket(List<LocationData> locationData,
                                     InteractionHand hand) implements Packet<OpenTempadScreenPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "open_screen");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenTempadScreenPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenTempadScreenPacket> {

        @Override
        public void encode(OpenTempadScreenPacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(message.locationData, (buf, locationData) -> {
                buf.writeUUID(locationData.getId());
                buf.writeUtf(locationData.getName());
                buf.writeBlockPos(locationData.getBlockPos());
                buf.writeResourceKey(locationData.getLevelKey());
            });
            buffer.writeEnum(message.hand);
        }

        @Override
        public OpenTempadScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenTempadScreenPacket(buffer.readList((buf) -> {
                UUID id = buf.readUUID();
                String name = buf.readUtf();
                BlockPos pos = buf.readBlockPos();
                ResourceKey<Level> levelResourceKey = buf.readResourceKey(Registries.DIMENSION);
                return new LocationData(name, levelResourceKey, pos, id);
            }), buffer.readEnum(InteractionHand.class));
        }

        @Override
        public PacketContext handle(OpenTempadScreenPacket message) {
            return (player, level) -> Minecraft.getInstance().setScreen(new TempadScreen(message.hand, message.locationData));
        }
    }
}
