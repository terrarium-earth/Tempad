package me.codexadrian.tempad.common.options.containers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.util.Updatable;
import me.codexadrian.tempad.api.power.PowerSettings;
import me.codexadrian.tempad.api.power.PowerSettingsApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class TempadFluid implements ItemFluidContainer, Updatable {
    public static final Codec<TempadFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidHolder.CODEC.fieldOf("fluids").forGetter(TempadFluid::getFirstFluid),
            Codec.LONG.fieldOf("capacity").forGetter(TempadFluid::getCap)
    ).apply(instance, TempadFluid::new));

    public FluidHolder fluidHolder;
    public long capacity;
    public ItemStack container = ItemStack.EMPTY;

    public TempadFluid() {
        this(FluidHolder.empty(), 1000);
    }

    public TempadFluid(FluidHolder fluidHolder, long capacity) {
        this.fluidHolder = fluidHolder;
        this.capacity = capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = (int) capacity;
    }

    public void setupContainer(ItemStack container) {
        this.container = container;
    }

    public long getCap() {
        return capacity;
    }

    @Override
    public long insertFluid(FluidHolder fluid, boolean simulate) {
        if (fluidHolder.isEmpty()) {
            FluidHolder inserted = fluid.copyWithAmount(Mth.clamp(fluid.getFluidAmount(), 0, capacity));
            if (!simulate) fluidHolder = inserted;
            return inserted.getFluidAmount();
        } else if (fluidHolder.matches(fluid)) {
            long insertedAmount = Mth.clamp(fluid.getFluidAmount(), 0, capacity - fluidHolder.getFluidAmount());
            if (!simulate) fluidHolder.setAmount(fluidHolder.getFluidAmount() + insertedAmount);
            return insertedAmount;
        }
        return 0;
    }

    @Override
    public FluidHolder extractFluid(FluidHolder fluid, boolean simulate) {
        if (fluidHolder.isEmpty() || !fluidHolder.matches(fluid)) return FluidHolder.empty();
        long extractedAmount = Mth.clamp(fluid.getFluidAmount(), 0, fluidHolder.getFluidAmount());
        if (!simulate) {
            fluidHolder.setAmount(fluidHolder.getFluidAmount() - extractedAmount);
            if (fluidHolder.getFluidAmount() == 0) fluidHolder.setFluid(Fluids.EMPTY);
        }
        return fluid.copyWithAmount(extractedAmount);
    }

    @Override
    public void setFluid(int slot, FluidHolder fluid) {
        if (slot == 0) {
            this.fluidHolder = fluid;
        }
    }

    @Override
    public List<FluidHolder> getFluids() {
        return List.of(fluidHolder);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return fluidHolder.isEmpty();
    }

    @Override
    public FluidContainer copy() {
        return new TempadFluid(fluidHolder, capacity);
    }

    @Override
    public long getTankCapacity(int tankSlot) {
        return 1000;
    }

    @Override
    public void fromContainer(FluidContainer container) {
        this.fluidHolder = container.getFirstFluid().copyHolder();
        this.capacity = (int) container.getTankCapacity(0);
    }

    @Override
    public long extractFromSlot(FluidHolder fluidHolder, FluidHolder toExtract, Runnable snapshot) {
        if (fluidHolder.matches(toExtract) && !fluidHolder.isEmpty()) {
            long extracted = Mth.clamp(toExtract.getFluidAmount(), 0, fluidHolder.getFluidAmount());
            snapshot.run();
            fluidHolder.setAmount(fluidHolder.getFluidAmount() - extracted);
            if (fluidHolder.getFluidAmount() == 0) fluidHolder.setFluid(Fluids.EMPTY);
            return extracted;
        }
        return 0;
    }

    @Override
    public long extractFromSlot(int slot, FluidHolder toExtract, boolean simulate) {
        return extractFluid(toExtract, simulate).getFluidAmount();
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
    public FluidSnapshot createSnapshot() {
        return new Snapshot(fluidHolder);
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

    }

    @Override
    public FluidHolder getFirstFluid() {
        return fluidHolder;
    }

    @Override
    public void update() {

    }

    @Override
    public ItemStack getContainerItem() {
        return container;
    }

    public static class Snapshot implements FluidSnapshot {
        FluidHolder holder;

        public Snapshot(FluidHolder holder) {
            this.holder = holder;
        }

        @Override
        public void loadSnapshot(FluidContainer container) {
            container.setFluid(0, holder.copyHolder());
        }
    }
}
