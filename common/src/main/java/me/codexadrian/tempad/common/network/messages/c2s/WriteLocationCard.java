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
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public record WriteLocationCard(BlockPos printerLocation, UUID locationId) implements Packet<WriteLocationCard> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<WriteLocationCard> type() {
        return HANDLER;
    }

    public static class Handler extends CodecPacketType<WriteLocationCard> implements ServerboundPacketType<WriteLocationCard> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "write_location_card");
        public static final ByteCodec<WriteLocationCard> CODEC = ObjectByteCodec.create(
            CodecUtils.BLOCK_POS.fieldOf(WriteLocationCard::printerLocation),
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
                if (player.level().getBlockEntity(message.printerLocation()) instanceof LocationPrinterBlockEntity printer) {
                    ItemStack input = printer.container.getItem(0);
                    ItemStack output = printer.container.getItem(1);
                    if (input.is(TempadRegistry.LOCATION_CARD.get())) {
                        if (output.isEmpty()) {
                            ItemStack newCard = new ItemStack(TempadRegistry.LOCATION_CARD.get());
                            LocationCard.setLocation(newCard, location, player.getDisplayName().getString());
                            input.shrink(1);
                            printer.container.setItem(1, newCard);
                            if (input.isEmpty()) {
                                printer.container.setItem(0, ItemStack.EMPTY);
                            } else {
                                printer.container.setItem(0, input);
                            }
                        } else {
                            if (output.is(TempadRegistry.LOCATION_CARD.get())) {
                                LocationCard.setLocation(output, location, player.getDisplayName().getString());
                                printer.container.setItem(1, output);
                            }
                        }
                    }
                }
            };
        }
    }
}
