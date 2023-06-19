package me.codexadrian.tempad.common.data.tempad_options;

import me.codexadrian.tempad.common.TempadType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Range;

import java.util.List;

public class DurabilityOption extends TempadOption {
    public static final DurabilityOption NORMAL_INSTANCE = new DurabilityOption(TempadType.NORMAL);
    public static final DurabilityOption ADVANCED_INSTANCE = new DurabilityOption(TempadType.HE_WHO_REMAINS);
    protected DurabilityOption(TempadType type) {
        super(type);
    }

    @Override
    public boolean canTimedoorOpen(Player player, ItemStack stack) {
        return true;
    }

    @Override
    public void onTimedoorOpen(Player player, InteractionHand hand) {
        player.getItemInHand(hand).hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(hand));
    }

    @Override
    public void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.durability_cost", getType().durability - stack.getDamageValue(), getType().durability));
    }

    @Override
    public boolean isDurabilityBarVisible(ItemStack stack) {
        return stack.isDamaged();
    }

    @Override
    public @Range(from = 0, to = 13) int durabilityBarWidth(ItemStack stack) {
        return Math.round(13.0f - (float) stack.getDamageValue() * 13.0f / 100F);
    }
}
