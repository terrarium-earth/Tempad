package me.codexadrian.tempad.common.data.tempad_options;

import me.codexadrian.tempad.common.TempadType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class UnlimitedOption extends TempadOption {
    public static final UnlimitedOption NORMAL_INSTANCE = new UnlimitedOption(TempadType.NORMAL);
    public static final UnlimitedOption ADVANCED_INSTANCE = new UnlimitedOption(TempadType.HE_WHO_REMAINS);

    protected UnlimitedOption(TempadType type) {
        super(type);
    }

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return true;
    }

    @Override
    public void onTimedoorOpen(Player player, ItemStack stack) {

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
