package earth.terrarium.tempad.common.compat.waystones;

import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.api.locations.LocationProvider;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.config.CommonConfig;
import earth.terrarium.tempad.api.locations.LocationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        if (!CommonConfig.waystonesCompat) return Map.of();
        Map<UUID, LocationData> locations = new HashMap<>();
        for (var waystone : getWaystones(level.getPlayerByUUID(uuid))) {
            LocationData value = new LocationData(waystone.getName().getString(), waystone.getDimension(), waystone.getPos().above(2), 0, waystone.getWaystoneUid(), false, false);
            locations.put(waystone.getWaystoneUid(), value);
        }
        return locations;
    }
}
