package earth.terrarium.tempad.common.options.containers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.energy.base.EnergySnapshot;
import earth.terrarium.botarium.common.energy.impl.SimpleEnergySnapshot;
import earth.terrarium.botarium.util.Updatable;
import earth.terrarium.tempad.api.power.PowerSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class TempadEnergy implements EnergyContainer, Updatable {
    public static final Codec<TempadEnergy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("energy").forGetter(TempadEnergy::getStoredEnergy),
            Codec.LONG.fieldOf("maxEnergy").forGetter(TempadEnergy::getMaxCapacity)
    ).apply(instance, TempadEnergy::new));

    long energy;
    long maxEnergy;

    public TempadEnergy() {
        this(0, 100000);
    }

    public TempadEnergy(long energy, long maxEnergy) {
        this.energy = energy;
        this.maxEnergy = maxEnergy;
    }

    public void setupContainer(PowerSettings settings) {
        this.maxEnergy = settings.getFuelCapacity();
    }

    @Override
    public long insertEnergy(long maxAmount, boolean simulate) {
        long inserted = (long) Mth.clamp(maxAmount, 0, Math.min(maxInsert(), getMaxCapacity() - getStoredEnergy()));
        if (simulate) return inserted;
        this.setEnergy(this.energy + inserted);
        return inserted;
    }

    @Override
    public long extractEnergy(long maxAmount, boolean simulate) {
        long extracted = (long) Mth.clamp(maxAmount, 0, Math.min(maxExtract(), getStoredEnergy()));
        if (simulate) return extracted;
        this.setEnergy(this.energy - extracted);
        return extracted;
    }

    @Override
    public void setEnergy(long energy) {
        this.energy = Mth.clamp(energy, 0, getMaxCapacity());
    }

    @Override
    public long getStoredEnergy() {
        return this.energy;
    }

    @Override
    public long getMaxCapacity() {
        return this.maxEnergy;
    }

    @Override
    public long maxInsert() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long maxExtract() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean allowsInsertion() {
        return true;
    }

    @Override
    public boolean allowsExtraction() {
        return false;
    }

    @Override
    public EnergySnapshot createSnapshot() {
        return new SimpleEnergySnapshot(this);
    }

    @Override
    public void deserialize(CompoundTag nbt) {
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        return nbt;
    }

    @Override
    public void clearContent() {
        this.setEnergy(0);
    }

    @Override
    public void update() {

    }
}
