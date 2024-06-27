package earth.terrarium.tempad.common.items;

import dev.architectury.injectables.annotations.PlatformOnly;
import earth.terrarium.tempad.api.apps.TempadAppApi;
import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.options.FuelOptionsApi;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.api.power.PowerSettingsApi;
import earth.terrarium.tempad.client.config.TempadClientConfig;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.config.CommonConfig;
import earth.terrarium.tempad.api.locations.LocationData;
import earth.terrarium.tempad.common.entity.TimedoorEntity;
import earth.terrarium.tempad.common.registry.TempadRegistry;
import earth.terrarium.tempad.common.utils.ClientUtils;
import earth.terrarium.tempad.common.utils.LookupLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TempadItem extends Item {
    public TempadItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        var lookup = new LookupLocation("main", player.getInventory().findSlotMatchingItem(stack));
        if (!player.isShiftKeyDown()) {
            if (level.isClientSide) {
                TempadAppApi.API.getHomePage().openOnClient(player, lookup);
                return InteractionResultHolder.pass(stack);
            } else {
                return InteractionResultHolder.success(stack);
            }
        } else {
            if (level.isClientSide) {
                ClientUtils.openFavorited(lookup);
                return InteractionResultHolder.pass(stack);
            } else {
                return InteractionResultHolder.success(stack);
            }
        }
    }

    public static void summonTimeDoor(LocationData locationData, Player player, int color) {
        TimedoorEntity timedoor = new TimedoorEntity(TempadRegistry.TIMEDOOR_ENTITY.get(), player.level());
        var angle = player.getYHeadRot();
        float angleInRadians = (float) Math.toRadians(angle + 90);
        timedoor.setColor(color);
        timedoor.setTargetLocation(locationData);
        timedoor.setOwner(player.getUUID());
        var position = player.position();
        timedoor.setPos(position.x() + Mth.cos(angleInRadians) * CommonConfig.distanceFromPlayer, position.y(), position.z() + Mth.sin(angleInRadians) * CommonConfig.distanceFromPlayer);
        timedoor.setYRot(angle);
        player.level().addFreshEntity(timedoor);
        timedoor.playSound(Tempad.TIMEDOOR_SOUND.get());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        FuelOption option = FuelOptionsApi.API.findItemOption(stack);
        PowerSettings attachment = PowerSettingsApi.API.get(stack);
        option.addToolTip(stack, attachment, level, components, flag);
    }

    @Override
    public int getBarColor(@NotNull ItemStack $$0) {
        return TempadClientConfig.color;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return FuelOptionsApi.API.findItemOption(stack).isDurabilityBarVisible(stack, PowerSettingsApi.API.get(stack));
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return (int) (FuelOptionsApi.API.findItemOption(stack).getPercentage(stack, PowerSettingsApi.API.get(stack)) * 13);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        FuelOptionsApi.API.findItemOption(stack).tick(stack, PowerSettingsApi.API.get(stack), entity);
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
