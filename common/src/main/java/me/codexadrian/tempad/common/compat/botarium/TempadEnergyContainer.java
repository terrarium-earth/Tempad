package me.codexadrian.tempad.common.compat.botarium;

import earth.terrarium.botarium.common.energy.impl.SimpleEnergyContainer;

public class TempadEnergyContainer extends SimpleEnergyContainer {
    public TempadEnergyContainer(long maxCapacity) {
        super(maxCapacity);
    }

    @Override
    public long maxInsert() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long maxExtract() {
        return Integer.MAX_VALUE;
    }
}
