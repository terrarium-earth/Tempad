package me.codexadrian.tempad.common.compat.waystones;

import me.codexadrian.tempad.api.locations.LocationProvider;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.data.LocationData;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

public class WaystoneLocationProvider implements LocationProvider {
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "waystones");
    public static final WaystoneLocationProvider INSTANCE = new WaystoneLocationProvider();

    public static void init() {
        LocationApi.API.registerProvider(ID, INSTANCE);
    }

    public static Collection<Waystone> getWaystones(Player player) {
        return PlayerWaystoneManager.getActivatedWaystones(player);
    }

    @Override
    public Map<UUID, LocationData> getLocations(Level level, UUID uuid) {
        if (!TempadConfig.waystonesCompat) return Map.of();
        Map<UUID, LocationData> locations = new HashMap<>();
        for (var waystone : getWaystones(level.getPlayerByUUID(uuid))) {
            LocationData value = new LocationData(waystone.getName().getString(), waystone.getDimension(), waystone.getPos().above(2), waystone.getWaystoneUid(), ID, true, false, false);
            locations.put(waystone.getWaystoneUid(), value);
        }
        return locations;
    }
}
