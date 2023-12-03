package me.codexadrian.tempad.api.options.impl;

import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TimerOption extends TempadOption {
    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(Tempad.TIMER_NBT)) {
            long cooldownTimeTag = stack.getTag().getLong(Tempad.TIMER_NBT);
            Instant cooldownTime = Instant.ofEpochSecond(cooldownTimeTag);
            return Instant.now().isAfter(cooldownTime);
        }
        return true;
    }

    @Override
    public void onTimedoorOpen(Player player) {
        TeleportUtils.findTempad(player).getOrCreateTag().putLong(Tempad.TIMER_NBT, Instant.now().plusSeconds(TempadOptionApi.getFuelCost(TeleportUtils.findTempad(player))).getEpochSecond());
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        long cooldown = timeLeft(stack);
        if (cooldown > 0) {
            components.add(Component.translatable("tooltip.tempad.timeleft", Component.literal(DurationFormatUtils.formatDuration(cooldown, "mm:ss", true)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
        } else {
            components.add(Component.translatable("tooltip.tempad.fullycharged").withStyle(ChatFormatting.AQUA));
        }
        components.add(Component.translatable("tooltip.tempad.timer_cost", Component.literal(DurationFormatUtils.formatDuration(TempadOptionApi.getFuelCost(stack) * 1000L, "mm:ss", true)).withStyle(ChatFormatting.DARK_AQUA)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return !this.canTimedoorOpen(null, stack);
    }

    @Override
    public int durabilityBarWidth(ItemStack stack) {
        return (int) ((TempadOptionApi.getFuelCost(stack) * 1000.0 - timeLeft(stack)) / (TempadOptionApi.getFuelCost(stack) * 1000.0) * 13);
    }

    public static long timeLeft(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(Tempad.TIMER_NBT)) {
            long cooldownTimeTag = stack.getTag().getLong(Tempad.TIMER_NBT);
            Instant cooldownTime = Instant.ofEpochSecond(cooldownTimeTag);
            return Instant.now().until(cooldownTime, ChronoUnit.MILLIS);
        }
        return 0;
    }

    public static long getTimerNBT(ItemStack stack) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains(Tempad.TIMER_NBT)) {
                return stack.getTag().getLong(Tempad.TIMER_NBT);
            }
        }
        return 0;
    }
}
