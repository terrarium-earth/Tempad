package me.codexadrian.tempad.common.tempad;

import dev.architectury.injectables.annotations.PlatformOnly;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.TempadClient;
import me.codexadrian.tempad.common.TempadType;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.tempad_options.DurabilityOption;
import me.codexadrian.tempad.common.data.tempad_options.TempadOption;
import me.codexadrian.tempad.common.entity.TimedoorEntity;

import me.codexadrian.tempad.common.registry.TempadRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TempadItem extends Item {

    private final TempadType type;

    public TempadItem(TempadType type, Properties properties) {
        super(properties.defaultDurability(type.durability));
        this.type = type;
    }

    public TempadOption getOption() {
        return type == TempadType.NORMAL ? Tempad.getTempadConfig().getTempadOption() : Tempad.getTempadConfig().getHeWhoRemainsOption();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide) TempadClient.openScreen(interactionHand);
        return InteractionResultHolder.success(stack);
    }

    public static void summonTimeDoor(LocationData locationData, Player player, int color) {
        TimedoorEntity timedoor = new TimedoorEntity(TempadRegistry.TIMEDOOR_ENTITY.get(), player.level());
        var dir = player.getDirection();
        timedoor.setColor(color);
        timedoor.setLocation(locationData);
        timedoor.setOwner(player.getUUID());
        var position = player.position();
        var distance = Tempad.getTempadConfig().getDistanceFromPlayer();
        timedoor.setPos(position.x() + dir.getStepX() * distance, position.y(), position.z() + dir.getStepZ() * distance);
        timedoor.setYRot(dir.getOpposite().toYRot());
        player.level().addFreshEntity(timedoor);
        timedoor.playSound(Tempad.TIMEDOOR_SOUND.get());
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
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getOption().isDurabilityBarVisible(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return getOption().durabilityBarWidth(stack);
    }

    @Override
    public boolean canBeDepleted() {
        return this.getOption() instanceof DurabilityOption;
    }

    @PlatformOnly("forge")
    public boolean isDamaged(@NotNull ItemStack stack) {
        return canBeDepleted() && stack.getDamageValue() > 0;
    }
}
