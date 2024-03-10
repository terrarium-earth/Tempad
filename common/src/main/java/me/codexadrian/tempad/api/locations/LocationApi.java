package me.codexadrian.tempad.api.locations;

import me.codexadrian.tempad.api.ApiHelper;
import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LocationApi {
    LocationApi API = ApiHelper.load(LocationApi.class);

    Map<ResourceLocation, LocationProvider> getProviders();

    void registerProvider(ResourceLocation getterId, LocationProvider getter);

    LocationProvider getProvider(ResourceLocation getterId);

    List<LocationData> getAllAsList(Level level, UUID player);

    Map<UUID, LocationData> getAll(Level level, UUID player);

    LocationData get(Level level, UUID player, UUID location);

    void add(Level level, UUID player, LocationData location);

    void remove(Level level, UUID player, UUID location);

    void favorite(Level level, UUID player, UUID location);

    void unfavorite(Level level, UUID player);

    UUID getFavorite(Level level, UUID player);
}
