package me.codexadrian.tempad.network.messages;

import me.codexadrian.tempad.network.handlers.IPacketHandler;
import me.codexadrian.tempad.network.handlers.IPacket;
import me.codexadrian.tempad.data.LocationData;
import me.codexadrian.tempad.data.TempadComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

import static me.codexadrian.tempad.Constants.MODID;

public record AddLocationPacket(String name, InteractionHand hand) implements IPacket<AddLocationPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(MODID, "location");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<AddLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<AddLocationPacket> {

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
        public BiConsumer<MinecraftServer, Player> handle(AddLocationPacket message) {
            return (server, player) -> {
                ItemStack stack = player.getItemInHand(message.hand);
                var tempadLocation = new LocationData(message.name, player.level().dimension(), BlockPos.containing(player.getX(), Math.ceil(player.getY()), player.getZ()));
                TempadComponent.addStackLocation(stack, tempadLocation);
            };
        }
    }
}
