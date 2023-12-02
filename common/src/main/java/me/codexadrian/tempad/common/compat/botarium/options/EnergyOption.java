package me.codexadrian.tempad.common.compat.botarium.options;

import earth.terrarium.botarium.common.energy.EnergyApi;
import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Range;

import java.util.List;

public class EnergyOption extends TempadOption {
    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        EnergyContainer energyStorage = EnergyApi.getItemEnergyContainer(new ItemStackHolder(stack));
        if (energyStorage == null) return false;
        return energyStorage.getStoredEnergy() >= TempadOptionApi.getFuelCost(stack);
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        EnergyContainer energyStorage = EnergyApi.getItemEnergyContainer(new ItemStackHolder(stack));
        if (energyStorage == null) return;
        components.add(Component.translatable("tempad_option.tempad.energy", energyStorage.getStoredEnergy(), energyStorage.getMaxCapacity()).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("tempad_option.tempad.energy_cost", TempadOptionApi.getFuelCost(stack)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onTimedoorOpen(Player player) {
        ItemStack itemStack = TeleportUtils.findTempad(player);
        ItemStackHolder holder = new ItemStackHolder(itemStack);
        EnergyContainer energyStorage = EnergyApi.getItemEnergyContainer(holder);
        if (energyStorage == null) return;
        energyStorage.internalExtract(TempadOptionApi.getFuelCost(itemStack), false);
        if (holder.isDirty()) {
            TeleportUtils.findAndReplaceTempad(player, holder.getStack());
        }
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public @Range(from = 0, to = 13) int durabilityBarWidth(ItemStack stack) {
        EnergyContainer energyStorage = EnergyApi.getItemEnergyContainer(new ItemStackHolder(stack));
        if (energyStorage == null) return 0;
        return (int) (((double) energyStorage.getStoredEnergy() / energyStorage.getMaxCapacity()) * 13);
    }
}
