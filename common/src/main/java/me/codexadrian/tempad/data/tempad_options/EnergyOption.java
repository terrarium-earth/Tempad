package me.codexadrian.tempad.data.tempad_options;

import me.codexadrian.tempad.TempadType;
import me.codexadrian.tempad.items.EnergyItem;
import me.codexadrian.tempad.utils.ConfigUtils;
import net.minecraft.ChatFormatting;
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
            return energyItem.hasEnoughEnergy(stack, ConfigUtils.getOptionConfig(getType()).getEnergyCost());
        }
        return false;
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        if (stack.getItem() instanceof EnergyItem energyItem) {
            energyItem.drainEnergy(stack, ConfigUtils.getOptionConfig(getType()).getEnergyCost());
        }
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        if(stack.getItem() instanceof EnergyItem energyItem) {
            components.add(Component.translatable("tooltip.tempad.energy_info", energyItem.getEnergy(stack), energyItem.getMaxEnergy()).withStyle(ChatFormatting.GRAY));
        }
        components.add(Component.translatable("tooltip.tempad.energy_cost", ConfigUtils.getOptionConfig(getType()).getEnergyCost()).withStyle(ChatFormatting.DARK_GRAY));
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
        return ConfigUtils.getOptionConfig(getType()).getEnergyCapacity();
    }
}
