package me.codexadrian.tempad.common.items;

import earth.terrarium.baubly.common.Bauble;
import earth.terrarium.baubly.common.SlotInfo;
import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.options.FuelOptionsApi;
import me.codexadrian.tempad.api.power.PowerSettings;
import me.codexadrian.tempad.api.power.PowerSettingsApi;
import net.minecraft.world.item.ItemStack;

public class TempadBaubly implements Bauble {
    public static final TempadBaubly INSTANCE = new TempadBaubly();

    @Override
    public void tick(ItemStack stack, SlotInfo slot) {
        FuelOption option = FuelOptionsApi.API.findItemOption(stack);
        PowerSettings settings = PowerSettingsApi.API.get(stack);
        if (option != null && settings != null) {
            option.tick(stack, settings, slot.wearer());
        }
    }
}
