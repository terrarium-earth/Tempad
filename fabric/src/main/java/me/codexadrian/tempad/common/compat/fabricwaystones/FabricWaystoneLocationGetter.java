package me.codexadrian.tempad.common.compat.fabricwaystones;

// import wraith.fwaystones.FabricWaystones;


public class FabricWaystoneLocationGetter {
    public static void init() {
        /* TODO Enable this when Fabric Waystones is updated
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
         */
    }
}
