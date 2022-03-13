package me.codexadrian.tempad;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import static me.codexadrian.tempad.Constants.MODID;

public class ColorDataComponent implements AutoSyncedComponent {

    public static final ComponentKey<ColorDataComponent> COLOR_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(new ResourceLocation(MODID, "tempad_color_data"), ColorDataComponent.class);

    private int color = 0xff6f00;

    @Override
    public void readFromNbt(CompoundTag tag) {
        color = tag.getInt("tempadColor");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("tempadColor", this.color);
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
