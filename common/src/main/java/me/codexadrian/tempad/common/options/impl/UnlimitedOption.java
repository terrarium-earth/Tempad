package me.codexadrian.tempad.common.options.impl;

import me.codexadrian.tempad.api.options.FuelOption;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class UnlimitedOption implements FuelOption {

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return true;
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {}

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {}

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public double getPercentage(ItemStack stack) {
        return 1;
    }
}
