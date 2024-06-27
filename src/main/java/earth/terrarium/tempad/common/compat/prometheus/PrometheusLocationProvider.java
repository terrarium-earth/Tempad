package earth.terrarium.tempad.common.compat.prometheus;

import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.api.locations.LocationProvider;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.api.locations.LocationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrometheusLocationProvider implements LocationProvider {
    public static final PrometheusLocationProvider INSTANCE = new PrometheusLocationProvider();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "prometheus");

    public static void init() {
        LocationApi.API.registerProvider(ID, INSTANCE);
    }

    @Override
    public Map<UUID, LocationData> getLocations(Level level, UUID player) {
        Map<UUID, LocationData> locations = new HashMap<>();
        LocationsApi.API.getWarps(level.getServer()).forEach((name, globalPos) -> {
            UUID locationId = UUID.nameUUIDFromBytes(("warps" + globalPos.dimension() + globalPos.pos()).getBytes());
            locations.put(locationId, new LocationData(name, globalPos.dimension(), globalPos.pos(), 0, locationId, false, false));
        });
        if (level.getServer() != null) {
            ServerPlayer serverPlayer = level.getServer().getPlayerList().getPlayer(player);
            if (serverPlayer == null) return locations;
            LocationsApi.API.getHomes(serverPlayer).forEach((name, globalPos) -> {
                UUID locationId = UUID.nameUUIDFromBytes(("homes" + globalPos.dimension() + globalPos.pos()).getBytes());
                locations.put(locationId, new LocationData(name, globalPos.dimension(), globalPos.pos(), 0, locationId, true, false));
            });
        }
        return locations;
    }
}
