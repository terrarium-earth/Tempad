package me.codexadrian.tempad.common.compat.botarium.options.fabric;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidOptionImpl {
    @SuppressWarnings("UnstableApiUsage")
    public static Component getFluidName(Fluid fluid) {
        if (fluid == Fluids.EMPTY) {
            return Component.translatable("item.tempad.empty_tank");
        }
        return FluidVariantAttributes.getName(FluidVariant.of(fluid));
    }
}
