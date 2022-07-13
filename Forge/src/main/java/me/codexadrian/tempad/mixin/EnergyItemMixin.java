package me.codexadrian.tempad.mixin;

import me.codexadrian.tempad.ForgeEnergyStorage;
import me.codexadrian.tempad.tempad.EnergyItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EnergyItem.class)
public interface EnergyItemMixin extends EnergyItem, IForgeItem {
    @Override
    default int getEnergy(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    default void setEnergy(ItemStack stack, int energy) {
        stack.getCapability(CapabilityEnergy.ENERGY).filter(object -> object instanceof ForgeEnergyStorage).map(object -> (ForgeEnergyStorage) object).ifPresent(fenergy -> fenergy.setEnergy(energy));
    }

    @Override
    default @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ForgeEnergyStorage(stack, getMaxEnergy());
    }
}
