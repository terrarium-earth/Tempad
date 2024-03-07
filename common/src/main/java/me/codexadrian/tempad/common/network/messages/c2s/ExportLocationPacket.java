package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import me.codexadrian.tempad.common.items.LocationCard;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public record ExportLocationPacket(UUID location) implements Packet<ExportLocationPacket> {

    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<ExportLocationPacket> type() {
        return HANDLER;
    }

    public static class Handler implements ServerboundPacketType<ExportLocationPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "export");

        @Override
        public Class<ExportLocationPacket> type() {
            return ExportLocationPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public ExportLocationPacket decode(FriendlyByteBuf buf) {
            return new ExportLocationPacket(buf.readUUID());
        }

        @Override
        public void encode(ExportLocationPacket packet, FriendlyByteBuf buffer) {
            buffer.writeUUID(packet.location);
        }

        @Override
        public Consumer<Player> handle(ExportLocationPacket message) {
            return player -> {
                if (!TeleportUtils.hasTempad(player)) return;
                ItemStack itemInHand = TeleportUtils.findTempad(player);
                if (TeleportUtils.hasLocationCard(player) && itemInHand.getItem() instanceof TempadItem tempadItem && tempadItem.getOption().canTimedoorOpen(player, itemInHand)) {
                    LocationData locationData = TempadLocationHandler.getLocation(player.level(), player.getUUID(), message.location);
                    if (locationData.isDownloadable() && TempadConfig.allowExporting) {
                        ItemStack stack = new ItemStack(TempadRegistry.LOCATION_CARD.get());
                        LocationCard.setLocation(stack, locationData, player.getDisplayName().getString());
                        TeleportUtils.extractLocationCard(player);
                        player.getInventory().placeItemBackInInventory(stack);
                    }
                }
            };
        }
    }
}