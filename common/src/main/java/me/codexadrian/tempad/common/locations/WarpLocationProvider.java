package me.codexadrian.tempad.common.locations;

import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.api.locations.LocationProvider;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.WarpHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;

public class WarpLocationProvider implements LocationProvider {
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "warp");
    public static final WarpLocationProvider INSTANCE = new WarpLocationProvider();

    public static void init() {
        LocationApi.API.registerProvider(ID, INSTANCE);
    }

    @Override
    public Map<UUID, LocationData> getLocations(Level level, UUID player) {
        if (level instanceof ServerLevel serverLevel) {
            return WarpHandler.getWarps(serverLevel, player);
        }
        return Map.of();
    }
}
