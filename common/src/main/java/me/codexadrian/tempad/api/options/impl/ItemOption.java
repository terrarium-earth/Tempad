package me.codexadrian.tempad.api.options.impl;

import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemOption extends TempadOption {
    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return player.getInventory().contains(Tempad.TEMPAD_FUEL_TAG);
    }

    @Override
    public void onTimedoorOpen(Player player) {
        findItemStack(player).shrink(1);
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.item_option_info"));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }

    public ItemStack findItemStack(Player player) {
        for (ItemStack item : player.getInventory().items) {
            if (item.is(Tempad.TEMPAD_FUEL_TAG)) {
                return item;
            }
        }
        for (ItemStack item : player.getInventory().offhand) {
            if (item.is(Tempad.TEMPAD_FUEL_TAG)) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }
}
