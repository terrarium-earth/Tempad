package me.codexadrian.tempad.common.compat.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TempadTrinket implements Trinket {
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (stack.getItem() instanceof TempadItem tempadItem) {
            tempadItem.getOption().tick(stack, entity);
        }
    }
}
