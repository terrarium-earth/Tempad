package me.codexadrian.tempad.common.network.messages;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import java.util.UUID;

public record AddLocationPacket(String name, InteractionHand hand) implements Packet<AddLocationPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "location");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<AddLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<AddLocationPacket> {

        @Override
        public void encode(AddLocationPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.name);
            buffer.writeEnum(message.hand);
        }

        @Override
        public AddLocationPacket decode(FriendlyByteBuf buffer) {
            return new AddLocationPacket(buffer.readUtf(), buffer.readEnum(InteractionHand.class));
        }

        @Override
        public PacketContext handle(AddLocationPacket message) {
            return (player, level) -> {
                UUID uuid = CommonUtils.generate(id -> !TempadLocationHandler.containsLocation(level, player.getUUID(), id), UUID::randomUUID);
                var tempadLocation = new LocationData(message.name, player.level().dimension(), BlockPos.containing(player.getX(), Math.ceil(player.getY()), player.getZ()), uuid);
                TempadLocationHandler.addLocation(level, player.getUUID(), tempadLocation);
            };
        }
    }
}
