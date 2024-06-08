package earth.terrarium.tempad.common.utils;

import com.mojang.datafixers.util.Pair;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.baubly.common.BaubleUtils;
import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.options.FuelOptionsApi;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.api.power.PowerSettingsApi;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.config.ConfigCache;
import earth.terrarium.tempad.common.config.TempadConfig;
import earth.terrarium.tempad.common.items.LocationCard;
import earth.terrarium.tempad.common.items.TempadItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;

public class TeleportUtils {

    public static boolean mayTeleport(ResourceKey<Level> level, Player player) {
        if (player.isCreative()) return true;
        var lookup = player.level().registryAccess().lookup(Registries.DIMENSION);
        if (lookup.isPresent()) {
            var levelReference = lookup.get().get(level);
            if (levelReference.isPresent()) {
                if (levelReference.get().is(Tempad.TEMPAD_DIMENSION_BLACKLIST)) {
                    return false;
                };
            }
        }
        return level.equals(player.level().dimension()) || (player.level().isClientSide() ? ConfigCache.allowInterdimensionalTravel : TempadConfig.allowInterdimensionalTravel);
    }

    public static Pair<ItemStack, LookupLocation> findTempad(Player player) {
        Pair<ItemStack, LookupLocation> value = null;

        if (isBaubleModLoaded()) {
            Map<String, Container> baubleContainer = BaubleUtils.getBaubleContainer(player, TempadSlotIdentifier.INSTANCE);
            for (var values : baubleContainer.entrySet()) {
                for (int i = 0; i < values.getValue().getContainerSize(); i++) {
                    ItemStack stack = values.getValue().getItem(i);
                    FuelOption option = FuelOptionsApi.API.findItemOption(stack);
                    PowerSettings settings = PowerSettingsApi.API.get(stack);
                    if (option != null && settings != null) {
                        value = Pair.of(stack, new LookupLocation(values.getKey(), i));
                        if (option.canTimedoorOpen(stack, settings, player)) {
                            return value;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            FuelOption option = FuelOptionsApi.API.findItemOption(stack);
            PowerSettings settings = PowerSettingsApi.API.get(stack);
            if (option != null && settings != null) {
                value = Pair.of(stack, new LookupLocation("main", i));
                if (option.canTimedoorOpen(stack, settings, player)) {
                    return value;
                }
            }
        }

        return value;
    }

    public static ItemStack findTempad(Player player, LookupLocation location) {
        if (location.id().equals("main")) {
            return player.getInventory().getItem(location.slot());
        } else if (isBaubleModLoaded()) {
            Container baubleContainer = BaubleUtils.getBaubleContainer(player, location.id());
            if (baubleContainer != null) {
                return baubleContainer.getItem(location.slot());
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean hasTempad(Player player) {
        return findTempad(player) != null;
    }

    public static boolean hasLocationCard(Player player) {
        if (player.isCreative()) return true;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof LocationCard && LocationCard.getLocation(stack) == null) {
                return true;
            }
        }
        return false;
    }

    public static void extractLocationCard(Player player) {
        if (player.isCreative()) return;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof LocationCard && LocationCard.getLocation(stack) == null) {
                stack.shrink(1);
                if (stack.getCount() == 0) {
                    stack = ItemStack.EMPTY;
                }
                player.getInventory().setItem(i, stack);
                return;
            }
        }
    }

    @ExpectPlatform
    public static boolean isBaubleModLoaded() {
        throw new NotImplementedException();
    }
}
