package me.codexadrian.tempad.common.compat.fabricwaystones;

import me.codexadrian.tempad.api.locations.LocationsApi;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import wraith.fwaystones.FabricWaystones;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FabricWaystoneLocationGetter {
    public static void init() {
        LocationsApi.registerLocationGetter("tempad:fabric_waystones", (level, playerId) -> {
            if (!TempadConfig.fabricWaystonesCompat) return Map.of();
            var locations = new HashMap<UUID, LocationData>();
            Player player = level.getPlayerByUUID(playerId);
            if (player != null) {
                FabricWaystones.WAYSTONE_STORAGE.WAYSTONES
                    .values()
                    .stream()
                    .filter(waystoneValue -> waystoneValue.isGlobal() || waystoneValue.getEntity().getOwner() == null || waystoneValue.getEntity().getOwner().equals(player.getUUID()))
                    .forEach(waystoneValue -> {
                        UUID locationId = UUID.nameUUIDFromBytes(waystoneValue.getHash().getBytes());
                        LocationData value = new LocationData(waystoneValue.getWaystoneName(), ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(waystoneValue.getWorldName())), waystoneValue.way_getPos().above(2), locationId, true, false, false);
                        locations.put(locationId, value);
                    }
                );
            }
            return locations;
        });
    }
}
