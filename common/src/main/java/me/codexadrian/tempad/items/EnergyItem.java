package me.codexadrian.tempad.items;

import net.minecraft.world.item.ItemStack;

public interface EnergyItem {

    default int getEnergy(ItemStack stack) {
        return 0;
    }

    default void setEnergy(ItemStack stack, int energy) {
    }

    int getMaxEnergy();

    default void drainEnergy(ItemStack stack, int energy) {
        this.setEnergy(stack, this.getEnergy(stack) - energy);
    }

    default boolean hasEnoughEnergy(ItemStack stack, int energy) {
        return getEnergy(stack) >= energy;
    }
}