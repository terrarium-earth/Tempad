package me.codexadrian.tempad;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColorDataProvider implements ICapabilitySerializable<CompoundTag> {
    private ColorData color = new ColorData();

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeTempad.INSTANCE.orEmpty(cap, LazyOptional.of(() -> color));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tempadColor", this.color.color);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        color.color = nbt.getInt("tempadColor");
    }
}
