package me.codexadrian.tempad.common.items;

import me.codexadrian.tempad.api.options.FuelOption;

public interface TempadPower {
    FuelOption getOption();
    int getFuelCost();
    int getFuelCapacity();
}
