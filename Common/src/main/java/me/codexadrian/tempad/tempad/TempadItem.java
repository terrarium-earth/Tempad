package me.codexadrian.tempad.tempad;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
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
        var position = player.blockPosition();
        double xOffset = player.getX() - position.getX();
        double yOffset = player.getY() - position.getY();
        double zOffset = player.getZ() - position.getZ();
        BlockPos offsetPos = position.relative(dir, Tempad.getTempadConfig().getDistanceFromPlayer());
        timedoor.setPos(offsetPos.getX() + xOffset, offsetPos.getY() + yOffset, offsetPos.getZ() + zOffset);
        timedoor.setYRot(dir.getOpposite().toYRot());
        player.level.addFreshEntity(timedoor);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        MutableComponent componentToAdd;
        if(stack.hasTag() && stack.getTag() != null) { //IntelliJ wouldnt leave me alone
            if(stack.getTag().contains("CooldownTime")) {
                String cooldownTimeTag = stack.getTag().getString("CooldownTime");
                LocalDateTime cooldownTime = LocalDateTime.parse(cooldownTimeTag);
                LocalDateTime timeNow = LocalDateTime.now();
                Duration between = Duration.between(cooldownTime, timeNow);
                int maxCooldown = Tempad.getTempadConfig().getCooldownTime();
                if(between.toSeconds() < maxCooldown) {
                    between = Duration.of(maxCooldown, ChronoUnit.SECONDS).minus(between);
                    componentToAdd = new TranslatableComponent("tooltip.tempad.timeleft").append(" " + between.toMinutesPart() + ":" + between.toSecondsPart());
                } else {
                    componentToAdd = new TranslatableComponent("tooltip.tempad.fullycharged");
                }
            } else {
                componentToAdd = new TranslatableComponent("tooltip.tempad.fullycharged");
            }
        } else {
            componentToAdd = new TranslatableComponent("tooltip.tempad.fullycharged");
        }
        components.add(componentToAdd.withStyle(ChatFormatting.GRAY));
    }
}
