package me.codexadrian.tempad.common.options.impl;

import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.options.FuelOptionsApi;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EnergyOption implements FuelOption {
    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        EnergyContainer energyStorage = EnergyContainer.of(new ItemStackHolder(stack));
        if (energyStorage == null) return false;
        return energyStorage.getStoredEnergy() >= FuelOptionsApi.API.getFuelCost(stack);
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        EnergyContainer energyStorage = EnergyContainer.of(new ItemStackHolder(stack));
        if (energyStorage == null) return;
        components.add(Component.translatable("tempad_option.tempad.energy", energyStorage.getStoredEnergy(), energyStorage.getMaxCapacity()).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("tempad_option.tempad.energy_cost", FuelOptionsApi.API.getFuelCost(stack)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        ItemStackHolder holder = new ItemStackHolder(stack);
        EnergyContainer energyStorage = EnergyContainer.of(holder);
        if (energyStorage == null) return;
        energyStorage.internalExtract(FuelOptionsApi.API.getFuelCost(stack), false);
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
        EnergyContainer energyStorage = EnergyContainer.of(new ItemStackHolder(stack));
        if (energyStorage == null) return 0;
        return (double) energyStorage.getStoredEnergy() / energyStorage.getMaxCapacity();
    }
}
