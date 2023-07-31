package me.codexadrian.tempad.common.compat.waystones;

import me.codexadrian.tempad.api.locations.LocationsApi;
import me.codexadrian.tempad.common.data.LocationData;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WaystoneLocationGetter {
    public static void init() {
        LocationsApi.registerLocationGetter("tempad:waystones", (level, uuid) -> {
            var locations = new HashMap<UUID, LocationData>();
            Player player = level.getPlayerByUUID(uuid);
            if (player != null) {
                List<IWaystone> waystones = PlayerWaystoneManager.getWaystones(player);
                for (var waystone : waystones) {
                    locations.put(waystone.getWaystoneUid(), new LocationData(waystone.getName(), waystone.getDimension(), waystone.getPos().above(2), waystone.getWaystoneUid()));
                }
            }
            return locations;
        });
    }
}
