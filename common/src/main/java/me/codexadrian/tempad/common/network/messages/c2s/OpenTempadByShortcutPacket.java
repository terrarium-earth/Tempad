package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.s2c.OpenTempadScreenPacket;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.UUID;

public record OpenTempadByShortcutPacket() implements Packet<OpenTempadByShortcutPacket> {
    public static Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "shortcut_screen");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenTempadByShortcutPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenTempadByShortcutPacket> {

        @Override
        public void encode(OpenTempadByShortcutPacket message, FriendlyByteBuf buffer) {
        }

        @Override
        public OpenTempadByShortcutPacket decode(FriendlyByteBuf buffer) {
            return new OpenTempadByShortcutPacket();
        }

        @Override
        public PacketContext handle(OpenTempadByShortcutPacket message) {
            return (player, level) -> {
                if (!TeleportUtils.hasTempad(player)) return;
                OpenTempadScreenPacket packet = new OpenTempadScreenPacket(new ArrayList<>(TempadLocationHandler.getLocations(level, player.getUUID()).values()), TempadLocationHandler.getFavorite(level, player.getUUID()));
                NetworkHandler.CHANNEL.sendToPlayer(packet, player);
            };
        }
    }
}
