package me.codexadrian.tempad.data.tempad_options;

import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemOption extends TempadOption {
    public static final ItemOption NORMAL_INSTANCE = new ItemOption(TempadType.NORMAL);
    public static final ItemOption ADVANCED_INSTANCE = new ItemOption(TempadType.HE_WHO_REMAINS);

    protected ItemOption(TempadType type) {
        super(type);
    }

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return player.getInventory().contains(Tempad.TEMPAD_FUEL_TAG);
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        findItemStack(player).shrink(1);
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(new TranslatableComponent("tooltip.tempad.item_option_info"));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public int durabilityBarWidth(ItemStack stack) {
        return 0;
    }

    public ItemStack findItemStack(Player player) {
        for (ItemStack item : player.getInventory().items) {
            if(item.is(Tempad.TEMPAD_FUEL_TAG)) {
                return item;
            }
        }
        for (ItemStack item : player.getInventory().offhand) {
            if(item.is(Tempad.TEMPAD_FUEL_TAG)) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }
}