package me.codexadrian.tempad.common.network.messages.c2s;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
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
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public record OpenTempadByShortcutPacket() implements Packet<OpenTempadByShortcutPacket> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<OpenTempadByShortcutPacket> type() {
        return HANDLER;
    }

    public static class Handler implements ServerboundPacketType<OpenTempadByShortcutPacket> {
        public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "shortcut_screen");

        @Override
        public Class<OpenTempadByShortcutPacket> type() {
            return OpenTempadByShortcutPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public OpenTempadByShortcutPacket decode(FriendlyByteBuf buffer) {
            return new OpenTempadByShortcutPacket();
        }

        @Override
        public void encode(OpenTempadByShortcutPacket packet, FriendlyByteBuf buffer) {}

        @Override
        public Consumer<Player> handle(OpenTempadByShortcutPacket message) {
            return player -> {
                if (!TeleportUtils.hasTempad(player))
                    return;

                OpenTempadScreenPacket packet = new OpenTempadScreenPacket(new ArrayList<>(TempadLocationHandler.getLocations(player.level(), player.getUUID()).values()), TempadLocationHandler.getFavorite(player.level(), player.getUUID()));
                NetworkHandler.CHANNEL.sendToPlayer(packet, player);
            };
        }
    }
}