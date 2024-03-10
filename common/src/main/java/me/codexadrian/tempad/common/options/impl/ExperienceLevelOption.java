package me.codexadrian.tempad.common.options.impl;

import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.options.FuelOptionsApi;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExperienceLevelOption implements FuelOption {

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return player.experienceLevel >= FuelOptionsApi.API.getFuelCost(stack);
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        player.giveExperienceLevels(-FuelOptionsApi.API.getFuelCost(stack));
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.experience_level_cost", FuelOptionsApi.API.getFuelCost(stack)));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }
}

