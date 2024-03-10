package me.codexadrian.tempad.common.compat.botarium;

import earth.terrarium.botarium.common.energy.EnergyApi;
import earth.terrarium.botarium.common.energy.impl.SimpleEnergyContainer;
import earth.terrarium.botarium.common.energy.impl.WrappedItemEnergyContainer;
import earth.terrarium.botarium.common.fluid.FluidApi;
import earth.terrarium.botarium.common.fluid.impl.WrappedItemFluidContainer;
import me.codexadrian.tempad.api.options.FuelOptionsApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.options.impl.EnergyOption;
import me.codexadrian.tempad.common.options.impl.FluidOption;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import net.minecraft.resources.ResourceLocation;

public class BotariumTempadOptionRegistry {
    public static void preInit() {
        FuelOptionsApi.API.register( new ResourceLocation(Tempad.MODID, "energy"), new EnergyOption());
        FuelOptionsApi.API.register( new ResourceLocation(Tempad.MODID, "fluid"), new FluidOption());
    }

    public static void postInit() {
        EnergyApi.registerEnergyItem(TempadRegistry.TEMPAD, stack -> {
            if (stack.getItem() instanceof TempadItem item && item.getOption() instanceof EnergyOption) {
                return new WrappedItemEnergyContainer(stack, new SimpleEnergyContainer(FuelOptionsApi.API.getFuelCapacity(stack), Integer.MAX_VALUE, Integer.MAX_VALUE));
            }
            return null;
        });
        FluidApi.registerFluidItem(TempadRegistry.TEMPAD, stack -> {
            if (stack.getItem() instanceof TempadItem item && item.getOption() instanceof FluidOption) {
                return new WrappedItemFluidContainer(stack, new TempadFluidContainer(FuelOptionsApi.API.getFuelCapacity(stack)));
            }
            return null;
        });

        EnergyApi.registerEnergyItem(TempadRegistry.CREATIVE_TEMPAD, stack -> {
            if (stack.getItem() instanceof TempadItem item && item.getOption() instanceof EnergyOption) {
                return new WrappedItemEnergyContainer(stack, new SimpleEnergyContainer(FuelOptionsApi.API.getFuelCapacity(stack), Integer.MAX_VALUE, Integer.MAX_VALUE));
            }
            return null;
        });
        FluidApi.registerFluidItem(TempadRegistry.CREATIVE_TEMPAD, stack -> {
            if (stack.getItem() instanceof TempadItem item && item.getOption() instanceof FluidOption) {
                return new WrappedItemFluidContainer(stack, new TempadFluidContainer(FuelOptionsApi.API.getFuelCapacity(stack)));
            }
            return null;
        });
    }
}
