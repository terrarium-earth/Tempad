package me.codexadrian.tempad.api.locations;

import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LocationProvider {
    Map<UUID, LocationData> getLocations(Level level, UUID player);
}
