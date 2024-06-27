package earth.terrarium.tempad.common.locations;

import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.api.locations.LocationProvider;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.api.locations.LocationData;
import earth.terrarium.tempad.common.data.WarpHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;

public class WarpLocationProvider implements LocationProvider {
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "warp");
    public static final WarpLocationProvider INSTANCE = new WarpLocationProvider();

    public static void init() {
        LocationApi.API.registerProvider(ID, INSTANCE);
    }

    @Override
    public Map<UUID, LocationData> getLocations(Level level, UUID player) {
        if (level instanceof ServerLevel serverLevel) {
            return WarpHandler.getWarps(serverLevel, player);
        }
        return Map.of();
    }
}
