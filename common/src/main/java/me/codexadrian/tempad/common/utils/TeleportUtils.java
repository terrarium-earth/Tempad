package me.codexadrian.tempad.common.utils;

import earth.terrarium.botarium.util.CommonHooks;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class TeleportUtils {

    public static boolean mayTeleport(ResourceKey<Level> level, Player player) {
        if (player.isCreative()) return true;
        return level.equals(player.level().dimension()) || (player.level().isClientSide() ? ConfigCache.allowInterdimensionalTravel : TempadConfig.allowInterdimensionalTravel);
    }

    public static ItemStack findAndReplaceTempad(Player player, @Nullable ItemStack replacementTempad) {
        AtomicReference<ItemStack> tempad = new AtomicReference<>(ItemStack.EMPTY);
        Consumer<ItemStack> setTempad = null;

        if (CommonHooks.isModLoaded("curios") || CommonHooks.isModLoaded("trinkets")) {
            setTempad = BaubleUtils.findTempadInBaubles(player, tempad::set);
        }

        //noinspection ConstantValue
        if (setTempad != null) {
            if (replacementTempad != null) {
                setTempad.accept(replacementTempad);
            }
            return tempad.get();
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof TempadItem tempadItem) {
                tempad.set(stack);
                int finalI = i;
                setTempad = (itemStack) -> player.getInventory().setItem(finalI, itemStack);

                if (tempadItem.getOption().canTimedoorOpen(player, tempad.get())) {
                    break;
                }
            }
        }

        if (replacementTempad != null && setTempad != null) {
            setTempad.accept(replacementTempad);
        }

        return tempad.get();
    }

    public static ItemStack findTempad(Player player) {
        return findAndReplaceTempad(player, null);
    }

    public static boolean hasTempad(Player player) {
        return !findTempad(player).isEmpty();
    }
}
