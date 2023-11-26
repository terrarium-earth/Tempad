package me.codexadrian.tempad.common.items;

import me.codexadrian.tempad.api.options.TempadOption;

public interface TempadPower {
    TempadOption getOption();
    int getFuelCost();
    int getFuelCapacity();
}
