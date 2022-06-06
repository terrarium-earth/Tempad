package me.codexadrian.tempad.tempad;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TempadItem extends Item {

    public TempadItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide) TempadClient.openScreen(interactionHand);
        return InteractionResultHolder.success(stack);
    }

    public static void summonTimeDoor(LocationData locationData, Player player, int color) {
        TimedoorEntity timedoor = new TimedoorEntity(Services.REGISTRY.getTimedoor(), player.level);
        var dir = player.getDirection();
        timedoor.setColor(color);
        timedoor.setLocation(locationData);
        timedoor.setOwner(player.getUUID());
        var position = player.position();
        var distance = Tempad.getTempadConfig().getDistanceFromPlayer();
        timedoor.setPos(position.x() + dir.getStepX() * distance, position.y(), position.z() + dir.getStepZ() * distance);
        timedoor.setYRot(dir.getOpposite().toYRot());
        player.level.addFreshEntity(timedoor);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        appendedText(stack, components);
    }

    public void appendedText(@NotNull ItemStack stack, @NotNull List<Component> components) {
        MutableComponent componentToAdd = null;
        if(stack.hasTag()) { //IntelliJ wouldnt leave me alone
            if(stack.getTag().contains(Constants.TIMER_NBT)) {
                if(!this.checkIfUsable(stack)) {
                    long seconds = Instant.now().until(Instant.ofEpochSecond(stack.getTag().getLong(Constants.TIMER_NBT)), ChronoUnit.MILLIS);
                    componentToAdd = new TranslatableComponent("tooltip.tempad.timeleft").append(DurationFormatUtils.formatDuration(seconds, "mm:ss", true));
                }
            }
        }
        componentToAdd = componentToAdd == null ? new TranslatableComponent("tooltip.tempad.fullycharged") : componentToAdd;
        components.add(componentToAdd.withStyle(ChatFormatting.GRAY));
    }

    public void handleUsage(ItemStack tempad) {
        tempad.getOrCreateTag().putLong(Constants.TIMER_NBT, Instant.now().plusSeconds(Tempad.getTempadConfig().getCooldownTime()).getEpochSecond());
    }

    public boolean checkIfUsable(ItemStack tempad) {
        if(tempad.hasTag() && tempad.getTag().contains(Constants.TIMER_NBT)) {
            long cooldownTimeTag = tempad.getTag().getLong(Constants.TIMER_NBT);
            Instant cooldownTime = Instant.ofEpochSecond(cooldownTimeTag);
            return Instant.now().isAfter(cooldownTime);
        }
        return true;
    }
}
