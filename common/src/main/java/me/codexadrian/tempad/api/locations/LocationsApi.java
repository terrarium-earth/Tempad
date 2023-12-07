package me.codexadrian.tempad.api.locations;

import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class LocationsApi {
    private static final Map<String, BiFunction<Level, UUID, Map<UUID, LocationData>>> LOCATION_GETTERS = new HashMap<>();

    public static void addLocation(Level level, UUID player, LocationData location) {
        TempadLocationHandler.addLocation(level, player, location);
    }

    public static void removeLocation(Level level, UUID player, UUID location) {
        TempadLocationHandler.removeLocation(level, player, location);
    }

    public static void registerLocationGetter(String getterId, BiFunction<Level, UUID, Map<UUID, LocationData>> getter) {
        LOCATION_GETTERS.put(getterId, getter);
    }

    public static void gatherLocations(Level level, UUID player, Map<UUID, LocationData> locations) {
        for (var getter : LOCATION_GETTERS.entrySet()) {
            try {
                locations.putAll(getter.getValue().apply(level, player));
            } catch (Exception e) {
                Tempad.LOG.error("Error gathering locations from " + getter.getKey(), e);
            }
        }
    }
}
