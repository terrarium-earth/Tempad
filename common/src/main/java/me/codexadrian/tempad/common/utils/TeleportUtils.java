package me.codexadrian.tempad.common.utils;

import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TeleportUtils {

    public static boolean mayTeleport(ResourceKey<Level> level, Player player) {
        if (player.isCreative()) return true;
        return level.equals(player.level().dimension()) || player.level().isClientSide() ? ConfigCache.allowInterdimensionalTravel : TempadConfig.allowInterdimensionalTravel;
    }

    public static ItemStack findAndReplaceTempad(Player player, @Nullable ItemStack replacementTempad) {
        ItemStack tempad = ItemStack.EMPTY;
        int tempadIndex = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof TempadItem tempadItem) {
                tempad = stack;
                tempadIndex = i;

                if (tempadItem.getOption().canTimedoorOpen(player, tempad)) {
                    break;
                }
            }
        }

        if (replacementTempad != null && !tempad.isEmpty()) {
            player.getInventory().setItem(tempadIndex, replacementTempad);
        }
        return tempad;
    }

    public static ItemStack findTempad(Player player) {
        return findAndReplaceTempad(player, null);
    }

    public static boolean hasTempad(Player player) {
        return !findTempad(player).isEmpty();
    }
}
