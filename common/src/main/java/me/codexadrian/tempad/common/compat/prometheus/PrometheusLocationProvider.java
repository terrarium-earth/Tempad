package me.codexadrian.tempad.common.compat.prometheus;

import earth.terrarium.prometheus.api.locations.LocationsApi;
import me.codexadrian.tempad.api.locations.LocationProvider;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.*;

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
