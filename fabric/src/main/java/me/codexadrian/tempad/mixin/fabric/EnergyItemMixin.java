package me.codexadrian.tempad.mixin.fabric;

import me.codexadrian.tempad.tempad.EnergyItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import team.reborn.energy.api.base.SimpleBatteryItem;

@Mixin(EnergyItem.class)
public interface EnergyItemMixin extends EnergyItem, SimpleBatteryItem {

    @Override
    default int getEnergy(ItemStack stack) {
        return (int) this.getStoredEnergy(stack);
    }

    @Override
    default void setEnergy(ItemStack stack, int energy) {
        this.setStoredEnergy(stack, energy);
    }

    @Override
    default long getEnergyCapacity() {
        return this.getMaxEnergy();
    }

    @Override
    default long getEnergyMaxInput() {
        return this.getMaxEnergy();
    }

    @Override
    default long getEnergyMaxOutput() {
        return this.getMaxEnergy();
    }
}
