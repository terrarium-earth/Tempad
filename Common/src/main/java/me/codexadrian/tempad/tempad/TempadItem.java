package me.codexadrian.tempad.tempad;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.data.LocationData;
import me.codexadrian.tempad.data.tempad_options.EnergyOption;
import me.codexadrian.tempad.data.tempad_options.TempadOption;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TempadItem extends Item implements EnergyItem {

    private final TempadOption option;

    public TempadItem(TempadOption option, Properties properties) {
        super(properties);
        this.option = option;
    }

    public TempadOption getOption() {
        return option;
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
        this.getOption().addToolTip(stack, level, components, flag);
    }

    @Override
    public int getBarColor(@NotNull ItemStack $$0) {
        return TempadClient.getClientConfig().getColor();
    }

    @Override
    public int getMaxEnergy() {
        if(this.option instanceof EnergyOption energy) {
            return energy.getMaxEnergy();
        }
        return 0;
    }
}
