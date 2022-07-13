package me.codexadrian.tempad.data.tempad_options;

import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExperienceOption extends TempadOption {
    public static final ExperienceOption NORMAL_INSTANCE = new ExperienceOption(TempadType.NORMAL);
    public static final ExperienceOption ADVANCED_INSTANCE = new ExperienceOption(TempadType.HE_WHO_REMAINS);

    public ExperienceOption(TempadType type) {
        super(type);
    }

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return player.totalExperience >= getType().getOptionConfig().getTempadExperienceCost();
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {
        player.giveExperiencePoints(-getType().getOptionConfig().getTempadExperienceCost());
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {

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
