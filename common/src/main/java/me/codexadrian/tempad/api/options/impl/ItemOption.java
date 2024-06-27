package me.codexadrian.tempad.api.options.impl;

import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemOption extends TempadOption {
    private int cost = 0;

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        cost = TempadOptionApi.getFuelCost(stack);
        int available = 0;

        for (ItemStack item : findItemStacks(player.getInventory())) {
            available += item.getCount();
        }

        return available >= cost;
    }

    @Override
    public void onTimedoorOpen(Player player) {
        Inventory inventory = player.getInventory();

        for (ItemStack item : findItemStacks(inventory)) {
            int count = item.getCount();

            int remainingCost = cost - count;
            if (remainingCost < 0) {
                // Logic for removing only a set quantity
                //  of the item rather than whole itemstack
                item.setCount(Math.abs(remainingCost));
                break;
            }

            cost -= item.getCount();
            inventory.removeItem(item);
        }
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.item_option_info"));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }

    public List<ItemStack> findItemStacks(Inventory inventory) {
        NonNullList<ItemStack> playerItems = inventory.items;
        NonNullList<ItemStack> playerOffhand = inventory.offhand;

        List<ItemStack> items = findItemStacks(playerItems);
        items.addAll(findItemStacks(playerOffhand));

        return items;
    }

    public List<ItemStack> findItemStacks(NonNullList<ItemStack> itemStacks) {
        List<ItemStack> items = new ArrayList<>();

        for (ItemStack item : itemStacks) {
            if (item.is(Tempad.TEMPAD_FUEL_TAG)) {
                items.add(item);
            }
        }

        return items;
   }
}
