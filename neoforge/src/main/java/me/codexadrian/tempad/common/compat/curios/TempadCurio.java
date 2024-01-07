package me.codexadrian.tempad.common.compat.curios;

import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class TempadCurio implements ICurioItem {
    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (stack.getItem() instanceof TempadItem tempadItem) {
            tempadItem.getOption().tick(stack, context.entity());
        }
    }
}
