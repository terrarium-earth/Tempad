package me.codexadrian.tempad.network.messages;

import me.codexadrian.tempad.blocks.TempadBlockEntity;
import me.codexadrian.tempad.data.LocationData;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.items.TempadItem;
import me.codexadrian.tempad.network.handlers.IPacket;
import me.codexadrian.tempad.network.handlers.IPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

import static me.codexadrian.tempad.Constants.MODID;

public record SummonBlockTimedoorPacket(BlockPos originPosition, Direction blockFacingDirection, Direction offsetDirection, ResourceLocation dimensionKey, BlockPos targetPos, int color) implements IPacket<SummonBlockTimedoorPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(MODID, "timedoor_block");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<SummonBlockTimedoorPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<SummonBlockTimedoorPacket> {

        @Override
        public void encode(SummonBlockTimedoorPacket message, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(message.originPosition);
            buffer.writeEnum(message.blockFacingDirection);
            buffer.writeEnum(message.offsetDirection);
            buffer.writeResourceLocation(message.dimensionKey);
            buffer.writeBlockPos(message.targetPos);
            buffer.writeVarInt(message.color);
        }

        @Override
        public SummonBlockTimedoorPacket decode(FriendlyByteBuf buffer) {
            return new SummonBlockTimedoorPacket(buffer.readBlockPos(), buffer.readEnum(Direction.class), buffer.readEnum(Direction.class), buffer.readResourceLocation(), buffer.readBlockPos(), buffer.readVarInt());
        }

        @Override
        public BiConsumer<MinecraftServer, Player> handle(SummonBlockTimedoorPacket message) {
            return (server, player) -> {
                if (player.getCommandSenderWorld().getBlockEntity(message.originPosition) instanceof TempadBlockEntity blockEntity && !blockEntity.hasLinkedTimedoor()) {
                    //TODO Handle timedoor consumption power
                    Vec3 vec3 = Vec3.atCenterOf(message.originPosition());
                    vec3 = vec3.add(0, message.offsetDirection == Direction.DOWN ? -1.5 : -.5, 0);
                    vec3 = vec3.relative(message.offsetDirection, 1);

                    TimedoorEntity entity = TempadItem.summonTimeDoor(new LocationData("", ResourceKey.create(Registry.DIMENSION_REGISTRY, message.dimensionKey), message.targetPos), player.getLevel(), vec3, message.blockFacingDirection, message.color, false, 0);
                    blockEntity.linkTimedoor(entity);
                }
            };
        }
    }
}
