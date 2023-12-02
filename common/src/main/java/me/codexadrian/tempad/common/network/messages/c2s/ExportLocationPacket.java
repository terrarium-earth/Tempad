package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
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
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record ExportLocationPacket(UUID location) implements Packet<ExportLocationPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "export");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ExportLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<ExportLocationPacket> {

        @Override
        public void encode(ExportLocationPacket message, FriendlyByteBuf buffer) {
            buffer.writeUUID(message.location);
        }

        @Override
        public ExportLocationPacket decode(FriendlyByteBuf buffer) {
            return new ExportLocationPacket(buffer.readUUID());
        }

        @Override
        public PacketContext handle(ExportLocationPacket message) {
            return (player, level) -> {
                if (!TeleportUtils.hasTempad(player)) return;
                ItemStack itemInHand = TeleportUtils.findTempad(player);
                if ((itemInHand.getItem() instanceof TempadItem tempadItem && tempadItem.getOption().canTimedoorOpen(player, itemInHand))) {
                    if (!player.getAbilities().instabuild)
                        tempadItem.getOption().onTimedoorOpen(player);
                    LocationData locationData = TempadLocationHandler.getLocation(level, player.getUUID(), message.location);
                    if (locationData.isDownloadable() && TempadConfig.allowExporting) {
                        ItemStack stack = new ItemStack(TempadRegistry.LOCATION_CARD.get());
                        LocationCard.setLocation(stack, locationData, player.getDisplayName().getString());
                        player.getInventory().placeItemBackInInventory(stack);
                    }
                }
            };
        }
    }
}
