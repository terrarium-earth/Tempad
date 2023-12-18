package me.codexadrian.tempad.common.utils.forge;

import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class TeleportUtilsImpl {
    @Nullable
    public static Consumer<ItemStack> findTempadInBaubles(Player player, Consumer<ItemStack> setTempad) {
        LazyOptional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
        AtomicReference<ItemStack> tempad = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<Consumer<ItemStack>> setter = new AtomicReference<>(null);
        if (curiosInventory.isPresent()) {
            ICuriosItemHandler handler = curiosInventory.orElseThrow(NullPointerException::new);
            handler.getCurios().forEach((identifier, stackHandler) -> {
                IDynamicStackHandler groupInventory = stackHandler.getStacks();
                for (int i = 0; i < groupInventory.getSlots(); i++) {
                    ItemStack stack = groupInventory.getStackInSlot(i);
                    if (stack.getItem() instanceof TempadItem tempadItem) {
                        boolean isViable = tempadItem.getOption().canTimedoorOpen(player, stack);
                        if (tempad.get() == ItemStack.EMPTY || isViable) {
                            setTempad.accept(stack);
                            tempad.set(stack);
                            int tempadSlot = i;
                            setter.set((itemStack) -> handler.getCurios().get(identifier).getStacks().setStackInSlot(tempadSlot, itemStack));
                            if (isViable) return;
                        }
                    }
                }
            });
        }
        return setter.get();
    }
}
