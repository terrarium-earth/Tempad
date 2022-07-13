package me.codexadrian.tempad;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeEnergyStorage implements IEnergyStorage, ICapabilityProvider {
    private final LazyOptional<IEnergyStorage> energyStorage = LazyOptional.of(() -> this);
    private final ItemStack stack;
    private final int maxEnergy;
    private final int maxExtract;
    private final int maxReceive;

    public ForgeEnergyStorage(ItemStack stack, int maxEnergy, int maxExtract, int maxReceive) {
        this.stack = stack;
        this.maxEnergy = maxEnergy;
        this.maxExtract = maxExtract;
        this.maxReceive = maxReceive;
    }

    public ForgeEnergyStorage(ItemStack stack, int maxEnergy) {
        this(stack, maxEnergy, maxEnergy, maxEnergy);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return capability == CapabilityEnergy.ENERGY ? energyStorage.cast() : LazyOptional.empty();
    }

    public void setEnergy(int energy) {
        this.stack.getOrCreateTag().putInt("Energy", energy);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!this.canReceive()) {
            return 0;
        }
        int energyReceived = Math.min(this.maxEnergy - this.getEnergyStored(), Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            this.setEnergy(getEnergyStored() + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!this.canExtract()) {
            return 0;
        }
        int energyExtracted = Math.min(this.getEnergyStored(), Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            this.setEnergy(getEnergyStored() - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return stack.getTag() != null ? stack.getTag().getInt("Energy") : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return this.maxEnergy;
    }

    @Override
    public boolean canExtract() {
        return this.getEnergyStored() > 0;
    }

    @Override
    public boolean canReceive() {
        return this.getEnergyStored() < this.maxEnergy;
    }
}
