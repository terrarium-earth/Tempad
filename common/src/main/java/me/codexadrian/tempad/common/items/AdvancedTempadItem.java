package me.codexadrian.tempad.common.items;

import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.options.FuelOptionsApi;
import me.codexadrian.tempad.common.config.ConfigCache;
import net.minecraft.resources.ResourceLocation;

public class AdvancedTempadItem extends TempadItem {
    public AdvancedTempadItem(Properties properties) {
        super(properties);
    }

    @Override
    public FuelOption getOption() {
        return FuelOptionsApi.API.getOption(ResourceLocation.tryParse(ConfigCache.advancedTempadFuelType));
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
