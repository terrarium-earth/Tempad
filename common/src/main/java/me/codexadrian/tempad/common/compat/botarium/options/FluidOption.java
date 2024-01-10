package me.codexadrian.tempad.common.compat.botarium.options;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class FluidOption extends TempadOption {
    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        FluidContainer fluidStorage = FluidContainer.of(new ItemStackHolder(stack));
        if (fluidStorage == null) return false;
        return !fluidStorage.getFluids().get(0).isEmpty() && fluidStorage.getFluids().get(0).getFluidAmount() >= TempadOptionApi.getFuelCost(stack) && fluidStorage.getFluids().get(0).is(Tempad.TEMPAD_LIQUID_FUEL_TAG);
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        FluidContainer fluidStorage = FluidContainer.of(new ItemStackHolder(stack));
        if (fluidStorage == null) return;
        components.add(Component.translatable("tempad_option.tempad.fluid", getFluidName(fluidStorage.getFluids().get(0).getFluid()), FluidConstants.toMillibuckets(fluidStorage.getFluids().get(0).getFluidAmount()), FluidConstants.toMillibuckets(fluidStorage.getTankCapacity(0))).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onTimedoorOpen(Player player) {
        ItemStack stack = TeleportUtils.findTempad(player);
        ItemStackHolder holder = new ItemStackHolder(stack);
        FluidContainer fluidStorage = FluidContainer.of(holder);
        if (fluidStorage == null) return;
        FluidHolder fluid = fluidStorage.getFluids().get(0);
        fluid.setAmount(TempadOptionApi.getFuelCost(stack));
        fluidStorage.extractFluid(fluid, false);
        if (holder.isDirty()) {
            TeleportUtils.findAndReplaceTempad(player, holder.getStack());
        }
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public double getPercentage(ItemStack stack) {
        FluidContainer fluidStorage = FluidContainer.of(new ItemStackHolder(stack));
        if (fluidStorage == null || fluidStorage.isEmpty()) return 0;
        return (double) fluidStorage.getFluids().get(0).getFluidAmount() / fluidStorage.getTankCapacity(0);
    }

    @ExpectPlatform
    public static Component getFluidName(Fluid fluid) {
        throw new NotImplementedException();
    }
}
