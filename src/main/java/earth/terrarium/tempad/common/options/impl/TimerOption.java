package earth.terrarium.tempad.common.options.impl;

import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.common.registry.TempadData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.List;

public class TimerOption implements FuelOption {
    public static final String ID = "Timer";

    @Override
    public boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        return TempadData.TIMER.getData(dataHolder).isFinished();
    }

    @Override
    public void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        TempadData.TIMER.getData(dataHolder).setTime(attachment.getFuelCost());
    }

    @Override
    public void addToolTip(Object dataHolder, PowerSettings attachment, Level level, List<Component> components, TooltipFlag flag) {
        long cooldown = timeLeft(dataHolder);
        if (cooldown > 0) {
            components.add(Component.translatable("tooltip.tempad.timeleft", Component.literal(DurationFormatUtils.formatDuration(cooldown * 1000, "mm:ss", true)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
        } else {
            components.add(Component.translatable("tooltip.tempad.fullycharged").withStyle(ChatFormatting.AQUA));
        }
        components.add(Component.translatable("tooltip.tempad.timer_cost", Component.literal(DurationFormatUtils.formatDuration(attachment.getFuelCost() * 1000L, "mm:ss", true)).withStyle(ChatFormatting.DARK_AQUA)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isDurabilityBarVisible(Object dataHolder, PowerSettings attachment) {
        return timeLeft(dataHolder) > 0;
    }

    @Override
    public double getPercentage(Object dataHolder, PowerSettings attachment) {
        return Math.max(0, 1 - (double) timeLeft(dataHolder) / (double) attachment.getFuelCost());
    }

    public long timeLeft(Object holder) {
        return TempadData.TIMER.getData(holder).getTime();
    }

    @Override
    public void tick(Object dataHolder, PowerSettings attachment, Entity entity) {
        // tick the timer
        TempadData.TIMER.getData(dataHolder).tick();
    }
}
