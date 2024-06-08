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

import java.util.Optional;
import java.util.function.Consumer;

public record OpenFavoritedLocationPacket(int color, Optional<LookupLocation> lookup) implements Packet<OpenFavoritedLocationPacket> {
    public static final Handler HANDLER = new Handler();

    @Override
    public PacketType<OpenFavoritedLocationPacket> type() {
        return HANDLER;
    }

    public static class Handler extends CodecPacketType<OpenFavoritedLocationPacket> implements ServerboundPacketType<OpenFavoritedLocationPacket> {
        public static final ByteCodec<OpenFavoritedLocationPacket> CODEC = ObjectByteCodec.create(
            ByteCodec.VAR_INT.fieldOf(OpenFavoritedLocationPacket::color),
            LookupLocation.CODEC.optionalFieldOf(OpenFavoritedLocationPacket::lookup),
            OpenFavoritedLocationPacket::new
        );

        public Handler() {
            super(OpenFavoritedLocationPacket.class, new ResourceLocation(Tempad.MODID, "shortcut_favorited"), CODEC);
        }

        @Override
        public Consumer<Player> handle(OpenFavoritedLocationPacket message) {
            return player -> {
                ItemStack tempadStack = message.lookup.map(lookup -> lookup.findItem(player)).orElse(TeleportUtils.findTempad(player).getFirst());
                LocationData locationData = LocationApi.API.get(player.level(), player.getUUID(), LocationApi.API.getFavorite(player.level(), player.getUUID()));
                PowerSettings attachment = PowerSettingsApi.API.get(tempadStack);
                FuelOption option = FuelOptionsApi.API.findItemOption(tempadStack);
                if (locationData != null && attachment != null && option != null && option.canTimedoorOpen(tempadStack, attachment, player) && TeleportUtils.mayTeleport(locationData.levelKey(), player)) {
                    if (!player.getAbilities().instabuild) option.onTimedoorOpen(tempadStack, attachment, player);
                    TempadItem.summonTimeDoor(locationData, player, message.color);
                }
            };
        }
    }
}