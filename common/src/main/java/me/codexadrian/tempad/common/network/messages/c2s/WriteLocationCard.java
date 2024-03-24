package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.blocks.LocationPrinterBlockEntity;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.items.LocationCard;
import me.codexadrian.tempad.common.menu.PrinterMenu;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public record WriteLocationCard(UUID locationId) implements Packet<WriteLocationCard> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<WriteLocationCard> type() {
        return HANDLER;
    }

    public static class Handler extends CodecPacketType<WriteLocationCard> implements ServerboundPacketType<WriteLocationCard> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "write_location_card");
        public static final ByteCodec<WriteLocationCard> CODEC = ObjectByteCodec.create(
            ByteCodec.UUID.fieldOf(WriteLocationCard::locationId),
            WriteLocationCard::new
        );

        public Handler() {
            super(WriteLocationCard.class, ID, CODEC);
        }

        @Override
        public Consumer<Player> handle(WriteLocationCard message) {
            return player -> {
                LocationData location = LocationApi.API.get(player.level(), player.getUUID(), message.locationId());
                if (location == null) return;
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof PrinterMenu printer) {
                    ItemStack input = printer.container.getStackInSlot(0);
                    ItemStack output = printer.container.getStackInSlot(1);
                    if (output.isEmpty()) {
                        if (input.is(TempadRegistry.LOCATION_CARD.get())) {
                            ItemStack itemStack = printer.container.extractFromSlot(0, 1, false);
                            LocationCard.setLocation(itemStack, location, player.getDisplayName().getString());
                            if (!itemStack.isEmpty()) {
                                printer.container.insertIntoSlot(1, itemStack, false);
                            }
                        }
                    } else if (output.is(TempadRegistry.LOCATION_CARD.get())) {
                        LocationCard.setLocation(output, location, player.getDisplayName().getString());
                        printer.container.setStackInSlot(1, output);
                    }
                }
            };
        }
    }
}
