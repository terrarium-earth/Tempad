package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record OpenFavoritedLocationPacket(int color) implements Packet<OpenFavoritedLocationPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "shortcut_favorited");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenFavoritedLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenFavoritedLocationPacket> {

        @Override
        public void encode(OpenFavoritedLocationPacket message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.color);
        }

        @Override
        public OpenFavoritedLocationPacket decode(FriendlyByteBuf buffer) {
            return new OpenFavoritedLocationPacket(buffer.readVarInt());
        }

        @Override
        public PacketContext handle(OpenFavoritedLocationPacket message) {
            return (player, level) -> {
                ItemStack tempadStack = TeleportUtils.findTempad(player);
                LocationData locationData = TempadLocationHandler.getLocation(level, player.getUUID(), TempadLocationHandler.getFavorite(level, player.getUUID()));
                if (locationData != null && tempadStack.getItem() instanceof TempadItem tempadItem && tempadItem.getOption().canTimedoorOpen(player, tempadStack) && TeleportUtils.mayTeleport(locationData.getLevelKey(), player)) {
                    if (!player.getAbilities().instabuild) tempadItem.getOption().onTimedoorOpen(player);
                    TempadItem.summonTimeDoor(locationData, player, message.color);
                }
            };
        }
    }
}
