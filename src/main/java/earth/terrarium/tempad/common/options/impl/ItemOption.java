package earth.terrarium.tempad.common.options.impl;

import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.common.Tempad;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemOption implements FuelOption {
    @Override
    public boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        return player.getInventory().contains(Tempad.TEMPAD_FUEL_TAG);
    }

    @Override
    public void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        findItemStack(player).shrink(1);
    }

    @Override
    public void addToolTip(Object dataHolder, PowerSettings attachment, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.item_option_info"));
    }

    public ItemStack findItemStack(Player player) {
        for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
            ItemStack stack = player.getInventory().getItem(index);
            if (stack.is(Tempad.TEMPAD_FUEL_TAG)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
