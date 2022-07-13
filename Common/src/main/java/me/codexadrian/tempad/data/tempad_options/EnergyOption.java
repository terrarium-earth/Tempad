package me.codexadrian.tempad.data.tempad_options;

import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadType;
import me.codexadrian.tempad.tempad.EnergyItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EnergyOption extends TempadOption {
    public static final EnergyOption NORMAL_INSTANCE = new EnergyOption(TempadType.NORMAL);
    public static final EnergyOption ADVANCED_INSTANCE = new EnergyOption(TempadType.HE_WHO_REMAINS);
    //public instance of this class

    public EnergyOption(TempadType type) {
        super(type);
    }

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        if (stack.getItem() instanceof EnergyItem energyItem) {
            return energyItem.hasEnoughEnergy(stack, getType().getOptionConfig().getTempadEnergyCost());
        }
        return false;
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        if (stack.getItem() instanceof EnergyItem energyItem) {
            energyItem.drainEnergy(stack, getType().getOptionConfig().getTempadEnergyCost());
        }
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        //TODO add energy tooltip
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        if (stack.getItem() instanceof EnergyItem energyItem) {
            return energyItem.hasEnoughEnergy(stack, 1);
        }
        return false;
    }

    @Override
    public int durabilityBarWidth(ItemStack stack) {
        if (stack.getItem() instanceof EnergyItem energyItem) {
            return (int) (((double) energyItem.getEnergy(stack) / energyItem.getMaxEnergy()) * 13);
        }
        return 0;
    }

    public int getMaxEnergy() {
        return getType().getOptionConfig().getTempadEnergyCost();
    }
}
