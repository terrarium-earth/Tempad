package me.codexadrian.tempad.common.options.impl;

import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.options.FuelOptionsApi;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.List;

public class TimerOption implements FuelOption {
    public static final String ID = "Timer";

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return !stack.getOrCreateTag().contains(ID);
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        stack.getOrCreateTag().putLong(ID, FuelOptionsApi.API.getFuelCost(stack) * 20L);
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        long cooldown = timeLeft(stack);
        if (cooldown > 0) {
            components.add(Component.translatable("tooltip.tempad.timeleft", Component.literal(DurationFormatUtils.formatDuration(cooldown * 1000, "mm:ss", true)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
        } else {
            components.add(Component.translatable("tooltip.tempad.fullycharged").withStyle(ChatFormatting.AQUA));
        }
        components.add(Component.translatable("tooltip.tempad.timer_cost", Component.literal(DurationFormatUtils.formatDuration(FuelOptionsApi.API.getFuelCost(stack) * 1000L, "mm:ss", true)).withStyle(ChatFormatting.DARK_AQUA)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return !this.canTimedoorOpen(null, stack);
    }

    @Override
    public double getPercentage(ItemStack stack) {
        return Math.max(0, 1 - (double) timeLeft(stack) / (double) FuelOptionsApi.API.getFuelCost(stack));
    }

    public static long timeLeft(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(ID)) {
            return stack.getTag().getInt(ID) / 20;
        }
        return 0;
    }

    @Override
    public void tick(ItemStack stack, Entity entity) {
        // tick the timer
        if (stack.getTag() != null && stack.getTag().contains(ID)) {
            long cooldownTimeTag = stack.getTag().getLong(ID);
            if (cooldownTimeTag == 0) {
                stack.getTag().remove(ID);
            } else {
                stack.getTag().putLong(ID, cooldownTimeTag - 1);
            }
        }
    }
}
