package me.codexadrian.tempad.common.compat.botarium.options.neoforge;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidOptionImpl {
    public static Component getFluidName(Fluid fluid) {
        if (fluid == Fluids.EMPTY) {
            return Component.translatable("item.tempad.empty_tank");
        }
        return fluid.getFluidType().getDescription();
    }
}
