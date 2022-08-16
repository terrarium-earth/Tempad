package me.codexadrian.tempad.items;

import net.minecraft.world.item.Item;

public class TempadBattery extends Item implements EnergyItem {
    public TempadBattery(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxEnergy() {
        return 500000;
    }
}
