package earth.terrarium.tempad.common.network.messages.c2s;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.tempad.api.apps.TempadAppApi;
import earth.terrarium.tempad.common.utils.CodecUtils;
import earth.terrarium.tempad.common.utils.LookupLocation;
import earth.terrarium.tempad.common.utils.TeleportUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.Consumer;

public record RequestAppScreen(ResourceLocation appId, Optional<LookupLocation> lookupLocation) implements Packet<RequestAppScreen> {
    public static final Handler HANDLER = new Handler();

    public RequestAppScreen(ResourceLocation appId, LookupLocation lookupLocation) {
        this(appId, Optional.of(lookupLocation));
    }

    public RequestAppScreen(ResourceLocation appId) {
        this(appId, Optional.empty());
    }

    @Override
    public PacketType<RequestAppScreen> type() {
        return HANDLER;
    }

    public static class Handler extends CodecPacketType<RequestAppScreen> implements ServerboundPacketType<RequestAppScreen> {
        public static final ByteCodec<RequestAppScreen> CODEC = ObjectByteCodec.create(
            CodecUtils.RESOURCE_LOCATION.fieldOf(RequestAppScreen::appId),
            LookupLocation.CODEC.optionalFieldOf(RequestAppScreen::lookupLocation),
            RequestAppScreen::new
        );
        public Handler() {
            super(RequestAppScreen.class, new ResourceLocation(Tempad.MODID, "request_screen"), CODEC);
        }

        @Override
        public Consumer<Player> handle(RequestAppScreen message) {
            return player -> {
                LookupLocation lookup = message.lookupLocation.orElseGet(() -> TeleportUtils.findTempad(player).getSecond());
                TempadAppApi.API.getApp(message.appId).openOnServer(player, lookup);
            };
        }
    }
}