package me.codexadrian.tempad.common.items;

import earth.terrarium.baubly.common.Bauble;
import earth.terrarium.baubly.common.SlotInfo;
import net.minecraft.world.item.ItemStack;

public class TempadBaubly implements Bauble {
    public static final TempadBaubly INSTANCE = new TempadBaubly();

    @Override
    public void tick(ItemStack stack, SlotInfo slot) {
        if (stack.getItem() instanceof TempadItem tempadItem) {
            tempadItem.getOption().tick(stack, slot.wearer());
        }
    }
}
