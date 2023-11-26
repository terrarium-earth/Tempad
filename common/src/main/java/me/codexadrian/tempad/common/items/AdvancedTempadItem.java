package me.codexadrian.tempad.common.items;

import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import me.codexadrian.tempad.common.config.TempadConfig;

public class AdvancedTempadItem extends TempadItem {
    public AdvancedTempadItem(Properties properties) {
        super(properties);
    }

    @Override
    public TempadOption getOption() {
        return TempadOptionApi.getOption(TempadConfig.advancedTempadFuelType);
    }

    @Override
    public int getFuelCost() {
        return TempadConfig.advancedTempadfuelConsumptionValue;
    }

    @Override
    public int getFuelCapacity() {
        return TempadConfig.advancedTempadfuelCapacityValue;
    }
}
