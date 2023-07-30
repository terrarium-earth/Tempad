package me.codexadrian.tempad.common.compat.botarium;

import earth.terrarium.botarium.common.energy.EnergyApi;
import earth.terrarium.botarium.common.energy.impl.SimpleEnergyContainer;
import earth.terrarium.botarium.common.energy.impl.WrappedItemEnergyContainer;
import earth.terrarium.botarium.common.fluid.FluidApi;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedItemFluidContainer;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.compat.botarium.options.EnergyOption;
import me.codexadrian.tempad.common.compat.botarium.options.FluidOption;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.registry.TempadRegistry;

public class BotariumTempadOptionRegistry {
    public static void preInit() {
        TempadOptionApi.OPTION_REGISTRY.put("tempad:energy", new EnergyOption());
        TempadOptionApi.OPTION_REGISTRY.put("tempad:fluid", new FluidOption());
    }

    public static void postInit() {
        TempadOption option = TempadOptionApi.getOption(TempadConfig.tempadFuelType);
        if (option instanceof EnergyOption) {
            EnergyApi.registerEnergyItem(TempadRegistry.TEMPAD, stack -> new WrappedItemEnergyContainer(stack, new SimpleEnergyContainer(TempadOptionApi.getFuelCapacity(stack))));
        } else if (option instanceof FluidOption) {
            FluidApi.registerFluidItem(TempadRegistry.TEMPAD, stack -> new WrappedItemFluidContainer(stack, new SimpleFluidContainer(FluidHooks.buckets(TempadOptionApi.getFuelCapacity(stack)), 1, (integer, fluidHolder) -> fluidHolder.is(Tempad.TEMPAD_LIQUID_FUEL_TAG))));
        }

        TempadOption advancedOption = TempadOptionApi.getOption(TempadConfig.advancedTempadFuelType);
        if (advancedOption instanceof EnergyOption) {
            EnergyApi.registerEnergyItem(TempadRegistry.CREATIVE_TEMPAD, stack -> new WrappedItemEnergyContainer(stack, new SimpleEnergyContainer(TempadOptionApi.getFuelCapacity(stack))));
        } else if (advancedOption instanceof FluidOption) {
            FluidApi.registerFluidItem(TempadRegistry.CREATIVE_TEMPAD, stack -> new WrappedItemFluidContainer(stack, new SimpleFluidContainer(FluidHooks.buckets(TempadOptionApi.getFuelCapacity(stack)), 1, (integer, fluidHolder) -> fluidHolder.is(Tempad.TEMPAD_LIQUID_FUEL_TAG))));
        }
    }
}
