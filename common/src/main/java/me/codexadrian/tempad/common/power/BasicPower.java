package me.codexadrian.tempad.common.power;

import me.codexadrian.tempad.api.power.PowerSettings;
import me.codexadrian.tempad.common.config.ConfigCache;
import net.minecraft.resources.ResourceLocation;

public class BasicPower implements PowerSettings {
    public static final BasicPower INSTANCE = new BasicPower();

    @Override
    public ResourceLocation getOptionId() {
        return ResourceLocation.tryParse(ConfigCache.tempadFuelType);
    }

    @Override
    public int getFuelCost() {
        return ConfigCache.tempadFuelConsumptionValue;
    }

    @Override
    public int getFuelCapacity() {
        return ConfigCache.tempadFuelCapacityValue;
    }
}
