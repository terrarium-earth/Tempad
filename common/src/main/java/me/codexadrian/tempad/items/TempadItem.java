package me.codexadrian.tempad.items;

import dev.architectury.injectables.annotations.PlatformOnly;
import me.codexadrian.tempad.*;
import me.codexadrian.tempad.data.LocationData;
import me.codexadrian.tempad.data.tempad_options.DurabilityOption;
import me.codexadrian.tempad.data.tempad_options.EnergyOption;
import me.codexadrian.tempad.data.tempad_options.TempadOption;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.registry.TempadEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TempadItem extends Item implements EnergyItem {

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

    public static void summonTimeDoor(LocationData locationData, Player player, int color, boolean temporary) {
        summonTimeDoor(locationData, player.getLevel(), player.position(), player.getDirection(), color, temporary, Tempad.getTempadConfig().getDistanceFromPlayer());
    }

    public static TimedoorEntity summonTimeDoor(LocationData locationData, Level level, Vec3 position, Direction dir, int color, boolean temporary, int distance) {
        TimedoorEntity timedoor = new TimedoorEntity(temporary ? TempadEntities.TIMEDOOR_ENTITY_TYPE.get() : TempadEntities.BLOCK_TIMEDOOR_ENTITY_TYPE.get(), level);
        timedoor.setColor(color);
        timedoor.setLocation(locationData);
        timedoor.setPos(position.x() + dir.getStepX() * distance, position.y(), position.z() + dir.getStepZ() * distance);
        timedoor.setYRot(dir.getOpposite().toYRot());
        if(!temporary) timedoor.setClosingTime(-1);
        level.addFreshEntity(timedoor);
        timedoor.playSound(Tempad.TIMEDOOR_SOUND.get());
        return timedoor;
    }

    public static void summonTimeDoor(LocationData locationData, Player player, int color) {
        summonTimeDoor(locationData, player, color, true);
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
    public int getMaxEnergy() {
        if(this.getOption() instanceof EnergyOption energy) {
            return energy.getMaxEnergy();
        }
        return 0;
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
