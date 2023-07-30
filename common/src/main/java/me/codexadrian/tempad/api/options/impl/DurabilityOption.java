package me.codexadrian.tempad.api.options.impl;

import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Range;

import java.util.List;

public class DurabilityOption extends TempadOption {

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return true;
    }

    @Override
    public void onTimedoorOpen(Player player, InteractionHand hand, ItemStack stack) {
        stack.hurtAndBreak(TempadOptionApi.getFuelCost(stack), player, player1 -> player1.broadcastBreakEvent(hand));
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.durability_cost", TempadOptionApi.getFuelCost(stack) - stack.getDamageValue(), TempadOptionApi.getFuelCapacity(stack)));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return stack.isDamaged();
    }

    @Override
    public @Range(from = 0, to = 13) int durabilityBarWidth(ItemStack stack) {
        return Math.round(13.0f - (float) stack.getDamageValue() * 13.0f / 100F);
    }

    @Override
    public Item.Properties apply(Item.Properties properties, int fuelCost, int fuelCapacity) {
        return properties.durability(fuelCapacity);
    }
}
