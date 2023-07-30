package me.codexadrian.tempad.common.compat.botarium.options;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.energy.EnergyApi;
import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.fluid.FluidApi;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Range;

import java.util.List;

public class FluidOption extends TempadOption {
    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        EnergyContainer energyStorage = EnergyApi.getItemEnergyContainer(new ItemStackHolder(stack));
        if (energyStorage == null) return false;
        return energyStorage.getStoredEnergy() > TempadOptionApi.getFuelCost(stack);
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        ItemFluidContainer fluidStorage = FluidApi.getItemFluidContainer(new ItemStackHolder(stack));
        if (fluidStorage == null) return;
        components.add(Component.translatable("tempad_option.tempad.fluid", getFluidName(fluidStorage.getFluids().get(0).getFluid()), FluidHooks.toMillibuckets(fluidStorage.getFluids().get(0).getFluidAmount()), FluidHooks.toMillibuckets(fluidStorage.getTankCapacity(0))).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onTimedoorOpen(Player player, InteractionHand hand, ItemStack stack) {
        EnergyContainer energyStorage = EnergyApi.getItemEnergyContainer(new ItemStackHolder(stack));
        if (energyStorage == null) return;
        energyStorage.extractEnergy(TempadOptionApi.getFuelCost(stack), false);
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public @Range(from = 0, to = 13) int durabilityBarWidth(ItemStack stack) {
        ItemFluidContainer fluidStorage = FluidApi.getItemFluidContainer(new ItemStackHolder(stack));
        if (fluidStorage == null || fluidStorage.isEmpty()) return 0;
        return (int) (((double) fluidStorage.getFluids().get(0).getFluidAmount() / fluidStorage.getTankCapacity(0)) * 13);
    }

    @ExpectPlatform
    public static Component getFluidName(Fluid fluid) {
        throw new NotImplementedException();
    }
}
