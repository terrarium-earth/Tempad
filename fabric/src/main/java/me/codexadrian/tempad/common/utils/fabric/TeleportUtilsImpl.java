package me.codexadrian.tempad.common.utils.fabric;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class TeleportUtilsImpl {
    @Nullable
    public static Consumer<ItemStack> findTempadInBaubles(Player player, Consumer<ItemStack> setTempad) {
        Optional<TrinketComponent> optionalTrinketComponent = TrinketsApi.getTrinketComponent(player);
        AtomicReference<ItemStack> tempad = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<Consumer<ItemStack>> setter = new AtomicReference<>(null);
        if (optionalTrinketComponent.isPresent()) {
            var trinketComponent = optionalTrinketComponent.get();
            trinketComponent.getInventory().forEach((groupName, stringTrinketInventoryMap) -> {
                stringTrinketInventoryMap.forEach((slotName, trinketInventory) -> {
                    var inventorySize = trinketInventory.getContainerSize();
                    for (int i = 0; i < inventorySize; i++) {
                        var stack = trinketInventory.getItem(i);
                        if (stack.getItem() instanceof TempadItem tempadItem) {
                            boolean isViable = tempadItem.getOption().canTimedoorOpen(player, stack);
                            if (tempad.get() == ItemStack.EMPTY || isViable) {
                                tempad.set(stack);
                                setTempad.accept(stack);
                                int tempadSlot = i;
                                setter.set((itemStack) -> trinketComponent.getInventory().get(groupName).get(slotName).setItem(tempadSlot, itemStack));
                                if (isViable) return;
                            }
                        }
                    }
                });
            });
        }
        return setter.get();
    }
}
