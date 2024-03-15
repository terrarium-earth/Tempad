package me.codexadrian.tempad.common.locations;

import me.codexadrian.tempad.api.locations.LocationProvider;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

public class LocationApiImpl implements LocationApi {
    private final Map<ResourceLocation, LocationProvider> LOCATION_PROVIDER = new HashMap<>();

    @Override
    public void registerProvider(ResourceLocation getterId, LocationProvider getter) {
        LOCATION_PROVIDER.put(getterId, getter);
    }

    @Override
    public LocationProvider getProvider(ResourceLocation getterId) {
        return LOCATION_PROVIDER.get(getterId);
    }

    @Override
    public List<LocationData> getAllAsList(Level level, UUID player) {
        return new ArrayList<>(getAll(level, player).values());
    }

    @Override
    public Map<UUID, LocationData> getAll(Level level, UUID player) {
        Map<UUID, LocationData> locations = new HashMap<>();
        for (LocationProvider provider : LOCATION_PROVIDER.values()) {
            locations.putAll(provider.getLocations(level, player));
        }
        return locations;
    }


    @Override
    public LocationData get(Level level, UUID player, UUID location) {
        return getAll(level, player).get(location);
    }

    @Override
    public void add(Level level, UUID player, LocationData location) {
        TempadLocationHandler.addLocation(level, player, location);
    }

    @Override
    public void remove(Level level, UUID player, UUID location) {
        if (TempadLocationHandler.containsLocation(level, player, location)) {
            TempadLocationHandler.removeLocation(level, player, location);
        }
    }

    @Override
    public void favorite(Level level, UUID player, UUID location) {
        TempadLocationHandler.favoriteLocation(level, player, location);
    }

    @Override
    public void unfavorite(Level level, UUID player) {
        TempadLocationHandler.unfavoriteLocation(level, player);
    }

    @Override
    public UUID getFavorite(Level level, UUID player) {
        return TempadLocationHandler.getFavorite(level, player);
    }

    @Override
    public Map<ResourceLocation, LocationProvider> getProviders() {
        return LOCATION_PROVIDER;
    }
}
