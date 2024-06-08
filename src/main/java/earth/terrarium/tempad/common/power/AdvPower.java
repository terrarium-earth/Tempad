package earth.terrarium.tempad.common.power;

import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.common.config.ConfigCache;
import net.minecraft.resources.ResourceLocation;

public class AdvPower implements PowerSettings {
    public static final AdvPower INSTANCE = new AdvPower();

    @Override
    public ResourceLocation getOptionId() {
        return ResourceLocation.tryParse(ConfigCache.advancedTempadFuelType);
    }

    @Override
    public int getFuelCost() {
        return ConfigCache.advancedTempadfuelConsumptionValue;
    }

    @Override
    public int getFuelCapacity() {
        return ConfigCache.advancedTempadfuelCapacityValue;
    }
}
