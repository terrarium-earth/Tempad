package me.codexadrian.tempad.api.options;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Range;

import java.util.List;

public abstract class TempadOption {

    public abstract boolean canTimedoorOpen(Player player, ItemStack stack);

    public void onTimedoorOpen(Player player) {
    }

    public abstract void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag);

    public abstract boolean isDurabilityBarVisible(ItemStack stack);

    public int durabilityBarWidth(ItemStack stack) {
        return (int) (getPercentage(stack) * 13);
    }

    public double getPercentage(ItemStack stack) {
        return 1;
    }

    public Item.Properties apply(Item.Properties properties, int fuelCost, int fuelCapacity) {
        return properties;
    }

    public boolean depletesDurability() {
        return false;
    }
}
