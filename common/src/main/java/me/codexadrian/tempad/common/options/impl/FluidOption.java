package me.codexadrian.tempad.common.options.impl;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.power.PowerSettings;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.registry.TempadData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class FluidOption implements FuelOption {
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "fluid");

    @Override
    public boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        FluidContainer fluidStorage = TempadData.FLUID.getData(dataHolder);
        if (fluidStorage == null) return false;
        return fluidStorage.getFluids().get(0).getFluidAmount() >= attachment.getFuelCost() && fluidStorage.getFluids().get(0).is(Tempad.TEMPAD_LIQUID_FUEL_TAG);
    }

    @Override
    public void addToolTip(Object dataHolder, PowerSettings attachment, Level level, List<Component> components, TooltipFlag flag) {
        FluidContainer fluidStorage = TempadData.FLUID.getData(dataHolder);
        if (fluidStorage == null) return;
        components.add(Component.translatable("tempad_option.tempad.fluid", getFluidName(fluidStorage.getFluids().get(0).getFluid()), FluidConstants.toMillibuckets(fluidStorage.getFluids().get(0).getFluidAmount()), FluidConstants.toMillibuckets(fluidStorage.getTankCapacity(0))).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        var fluidStorage = TempadData.FLUID.getData(dataHolder);
        if (fluidStorage == null) return;
        FluidHolder fluid = fluidStorage.getFirstFluid().copyWithAmount(attachment.getFuelCost());
        fluidStorage.internalExtract(fluid, false);
    }

    @Override
    public boolean isDurabilityBarVisible(Object dataHolder, PowerSettings attachment) {
        return true;
    }

    @Override
    public double getPercentage(Object dataHolder, PowerSettings attachment) {
        FluidContainer fluidStorage = TempadData.FLUID.getData(dataHolder);
        if (fluidStorage == null || fluidStorage.isEmpty()) return 0;
        return (double) fluidStorage.getFluids().get(0).getFluidAmount() / fluidStorage.getTankCapacity(0);
    }

    @ExpectPlatform
    public static Component getFluidName(Fluid fluid) {
        throw new NotImplementedException();
    }
}
