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
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.registry.TempadRegistry;

public class BotariumTempadOptionRegistry {
    public static void preInit() {
        TempadOptionApi.OPTION_REGISTRY.put("tempad:energy", new EnergyOption());
        TempadOptionApi.OPTION_REGISTRY.put("tempad:fluid", new FluidOption());
    }

    public static void postInit() {
        EnergyApi.registerEnergyItem(TempadRegistry.TEMPAD, stack -> {
            if (stack.getItem() instanceof TempadItem item && item.getOption() instanceof EnergyOption) {
                return new WrappedItemEnergyContainer(stack, new SimpleEnergyContainer(TempadOptionApi.getFuelCapacity(stack)));
            }
            return null;
        });
        FluidApi.registerFluidItem(TempadRegistry.TEMPAD, stack -> {
            if (stack.getItem() instanceof TempadItem item && item.getOption() instanceof FluidOption) {
                return new WrappedItemFluidContainer(stack, new SimpleFluidContainer(TempadOptionApi.getFuelCapacity(stack), 1, (integer, fluidHolder) -> fluidHolder.is(Tempad.TEMPAD_LIQUID_FUEL_TAG)));
            }
            return null;
        });

        EnergyApi.registerEnergyItem(TempadRegistry.CREATIVE_TEMPAD, stack -> {
            if (stack.getItem() instanceof TempadItem item && item.getOption() instanceof EnergyOption) {
                return new WrappedItemEnergyContainer(stack, new SimpleEnergyContainer(TempadOptionApi.getFuelCapacity(stack)));
            }
            return null;
        });
        FluidApi.registerFluidItem(TempadRegistry.CREATIVE_TEMPAD, stack -> {
            if (stack.getItem() instanceof TempadItem item && item.getOption() instanceof FluidOption) {
                return new WrappedItemFluidContainer(stack, new SimpleFluidContainer(TempadOptionApi.getFuelCapacity(stack), 1, (integer, fluidHolder) -> fluidHolder.is(Tempad.TEMPAD_LIQUID_FUEL_TAG)));
            }
            return null;
        });
    }
}
