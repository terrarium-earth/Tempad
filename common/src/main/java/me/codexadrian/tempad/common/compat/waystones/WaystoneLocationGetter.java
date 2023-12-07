package me.codexadrian.tempad.common.compat.waystones;

import me.codexadrian.tempad.api.locations.LocationsApi;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.data.LocationData;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WaystoneLocationGetter {
    public static void init() {
        LocationsApi.registerLocationGetter("tempad:waystones", (level, uuid) -> {
            if (!TempadConfig.waystonesCompat) return Map.of();
            var locations = new HashMap<UUID, LocationData>();
            Player player = level.getPlayerByUUID(uuid);
            if (player != null) {
                List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
                for (var waystone : waystones) {
                    LocationData value = new LocationData(waystone.getName(), waystone.getDimension(), waystone.getPos().above(2), waystone.getWaystoneUid(), true, false, false);
                    locations.put(waystone.getWaystoneUid(), value);
                }
            }
            return locations;
        });
    }
}
