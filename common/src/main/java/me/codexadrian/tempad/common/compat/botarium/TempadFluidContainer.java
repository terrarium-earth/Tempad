package me.codexadrian.tempad.common.compat.botarium;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import me.codexadrian.tempad.common.Tempad;

import java.util.function.BiPredicate;
import java.util.function.IntToLongFunction;

public class TempadFluidContainer extends SimpleFluidContainer {
    public TempadFluidContainer(long maxAmount) {
        super(integer -> maxAmount, 1, (integer, fluidHolder) -> fluidHolder.is(Tempad.TEMPAD_LIQUID_FUEL_TAG));
    }
}
