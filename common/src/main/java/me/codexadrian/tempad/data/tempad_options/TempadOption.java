package me.codexadrian.tempad.data.tempad_options;

import me.codexadrian.tempad.TempadType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Range;

import java.util.List;

public abstract class TempadOption {
    private final TempadType type;

    protected TempadOption(TempadType type) {
        this.type = type;
    }

    public abstract boolean canTimedoorOpen(Player player, ItemStack stack);

    public void onTimedoorOpen(Player player, ItemStack stack) {

    }
    public void onTimedoorOpen(Player player, InteractionHand hand) {
        onTimedoorOpen(player, player.getItemInHand(hand));
    }

    public abstract void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag);

    public abstract boolean isDurabilityBarVisible(ItemStack stack);

    @Range(from = 0, to = 13)
    public abstract int durabilityBarWidth(ItemStack stack);

    public TempadType getType() {
        return type;
    }
}