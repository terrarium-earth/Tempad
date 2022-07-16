package me.codexadrian.tempad.data.tempad_options;

import me.codexadrian.tempad.TempadType;
import me.codexadrian.tempad.utils.ConfigUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExperienceLevelOption extends TempadOption {
    public static final ExperienceLevelOption NORMAL_INSTANCE = new ExperienceLevelOption(TempadType.NORMAL);
    public static final ExperienceLevelOption ADVANCED_INSTANCE = new ExperienceLevelOption(TempadType.HE_WHO_REMAINS);

    public ExperienceLevelOption(TempadType type) {
        super(type);
    }

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return player.experienceLevel >= ConfigUtils.getOptionConfig(getType()).getExperienceLevelCost();
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        player.giveExperienceLevels(-ConfigUtils.getOptionConfig(getType()).getExperienceLevelCost());
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.experience_level_cost", ConfigUtils.getOptionConfig(getType()).getExperienceLevelCost()));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public int durabilityBarWidth(ItemStack stack) {
        return 0;
    }
}

