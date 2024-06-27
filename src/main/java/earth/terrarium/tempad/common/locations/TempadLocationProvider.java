package earth.terrarium.tempad.common.locations;

import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.api.locations.LocationProvider;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.api.locations.LocationData;
import earth.terrarium.tempad.common.data.TempadLocationHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

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
