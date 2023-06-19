package me.codexadrian.tempad.common.data.tempad_options;

import me.codexadrian.tempad.common.TempadType;
import me.codexadrian.tempad.common.utils.ConfigUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExperiencePointsOption extends TempadOption {
    public static final ExperiencePointsOption NORMAL_INSTANCE = new ExperiencePointsOption(TempadType.NORMAL);
    public static final ExperiencePointsOption ADVANCED_INSTANCE = new ExperiencePointsOption(TempadType.HE_WHO_REMAINS);

    public ExperiencePointsOption(TempadType type) {
        super(type);
    }

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return player.totalExperience >= ConfigUtils.getOptionConfig(getType()).getExperienceCost();
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        player.giveExperiencePoints(-ConfigUtils.getOptionConfig(getType()).getExperienceCost());
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.experience_points_cost", ConfigUtils.getOptionConfig(getType()).getExperienceCost()));
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
