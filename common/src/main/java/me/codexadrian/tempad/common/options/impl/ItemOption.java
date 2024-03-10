package me.codexadrian.tempad.common.options.impl;

import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemOption implements FuelOption {
    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return player.getInventory().contains(Tempad.TEMPAD_FUEL_TAG);
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        findItemStack(player).shrink(1);
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.item_option_info"));
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
