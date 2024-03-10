package me.codexadrian.tempad.common.locations;

import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.api.locations.LocationProvider;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.TempadLocationHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TempadLocationProvider implements LocationProvider {
    public static final TempadLocationProvider INSTANCE = new TempadLocationProvider();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "primary");

    public static void init() {
        LocationApi.API.registerProvider(ID, INSTANCE);
    }

    @Override
    public Map<UUID, LocationData> getLocations(Level level, UUID player) {
        return TempadLocationHandler.getLocations(level, player);
    }
}
