package me.codexadrian.tempad.api.options.impl;

import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExperienceLevelOption extends TempadOption {

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return player.experienceLevel >= TempadOptionApi.getFuelCost(stack);
    }

    @Override
    public void onTimedoorOpen(Player player) {
        player.giveExperienceLevels(-TempadOptionApi.getFuelCost(TeleportUtils.findTempad(player)));
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.experience_level_cost", TempadOptionApi.getFuelCost(stack)));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public int durabilityBarWidth(ItemStack stack) {
        return 0;
    }
}

