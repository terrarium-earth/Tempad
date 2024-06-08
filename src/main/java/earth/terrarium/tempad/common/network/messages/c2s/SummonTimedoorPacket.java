package earth.terrarium.tempad.common.network.messages.c2s;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.options.FuelOptionsApi;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.api.power.PowerSettingsApi;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.data.LocationData;
import earth.terrarium.tempad.common.items.TempadItem;
import earth.terrarium.tempad.common.utils.LookupLocation;
import earth.terrarium.tempad.common.utils.TeleportUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public record SummonTimedoorPacket(UUID location, int color, LookupLocation lookup) implements Packet<SummonTimedoorPacket> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<SummonTimedoorPacket> type() {
        return HANDLER;
    }

    public static class Handler extends CodecPacketType<SummonTimedoorPacket> implements ServerboundPacketType<SummonTimedoorPacket> {
        public static final ByteCodec<SummonTimedoorPacket> CODEC = ObjectByteCodec.create(
                ByteCodec.UUID.fieldOf(SummonTimedoorPacket::location),
                ByteCodec.INT.fieldOf(SummonTimedoorPacket::color),
                LookupLocation.CODEC.fieldOf(SummonTimedoorPacket::lookup),
                SummonTimedoorPacket::new
        );

        public Handler() {
            super(SummonTimedoorPacket.class, new ResourceLocation(Tempad.MODID, "timedoor"), CODEC);
        }

        @Override
        public Consumer<Player> handle(SummonTimedoorPacket message) {
            return player -> {
                ItemStack itemInHand = TeleportUtils.findTempad(player, message.lookup);
                LocationData locationData = LocationApi.API.get(player.level(), player.getUUID(), message.location);
                FuelOption option = FuelOptionsApi.API.findItemOption(itemInHand);
                PowerSettings attachment = PowerSettingsApi.API.get(itemInHand);
                if (locationData != null && option != null && option.canTimedoorOpen(itemInHand, attachment, player) && TeleportUtils.mayTeleport(locationData.levelKey(), player)) {
                    if (!player.getAbilities().instabuild) option.onTimedoorOpen(itemInHand, attachment, player);
                    TempadItem.summonTimeDoor(locationData, player, message.color);
                }
            };
        }
    }
}