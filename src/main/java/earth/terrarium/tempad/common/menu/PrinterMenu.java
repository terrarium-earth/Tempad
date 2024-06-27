package earth.terrarium.tempad.common.menu;

import com.mojang.serialization.Codec;
import earth.terrarium.botarium.common.item.impl.SimpleItemContainer;
import earth.terrarium.botarium.common.item.utils.SlotItemContainer;
import earth.terrarium.tempad.common.containers.AutoUpdatingWrapper;
import earth.terrarium.tempad.common.containers.PrinterContainer;
import earth.terrarium.tempad.api.locations.LocationData;
import earth.terrarium.tempad.common.registry.TempadMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PrinterMenu extends AbstractContainerMenu {
    public static final int MAX_SLOTS = 2;

    public AutoUpdatingWrapper container;
    public Inventory playerInventory;
    public List<LocationData> locations;

    public PrinterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, new PrinterContainer(), LocationData.BYTE_CODEC.listOf().decode(buffer));
    }

    public PrinterMenu(int containerId, Inventory playerInventory, SimpleItemContainer container, List<LocationData> locations) {
        super(TempadMenus.PRINTER.get(), containerId);
        this.container = new AutoUpdatingWrapper(container);
        this.playerInventory = playerInventory;
        this.locations = locations;

        this.addSlot(new SlotItemContainer<>(this.container, 0, 10, 65));
        this.addSlot(new SlotItemContainer<>(this.container, 1, 54, 65));
        this.addPlayerInvSlots(this.playerInventory);
    }

    public static void test(Codec<? extends Integer> boo) {

    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        test(Codec.INT);
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();

            if (index < MAX_SLOTS) {
                if (!this.moveItemStackTo(slotItem, MAX_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, MAX_SLOTS, false)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        ItemStack itemStack;
        Slot slot;
        boolean bl = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }
        if (stack.isStackable()) {
            while (!stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex)) {
                slot = this.slots.get(i);
                itemStack = slot.getItem();
                if (!itemStack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= slot.getMaxStackSize()) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        slot.setChanged();
                        bl = true;
                    } else if (itemStack.getCount() < slot.getMaxStackSize()) {
                        stack.shrink(stack.getMaxStackSize() - itemStack.getCount());
                        itemStack.setCount(slot.getMaxStackSize());
                        slot.setChanged();
                        bl = true;
                    }
                }
                if (reverseDirection) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        if (!stack.isEmpty()) {
            i = reverseDirection ? endIndex - 1 : startIndex;
            while (reverseDirection ? i >= startIndex : i < endIndex) {
                slot = this.slots.get(i);
                itemStack = slot.getItem();
                if (itemStack.isEmpty() && slot.mayPlace(stack)) {
                    if (stack.getCount() > slot.getMaxStackSize()) {
                        slot.setByPlayer(stack.split(slot.getMaxStackSize()));
                    } else {
                        slot.setByPlayer(stack.split(stack.getCount()));
                    }
                    slot.setChanged();
                    bl = true;
                    break;
                }
                if (reverseDirection) {
                    --i;
                    continue;
                }
                ++i;
            }
        }
        return bl;
    }


    protected void addPlayerInvSlots(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 24 + j * 18, 119 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 24 + k * 18, 177));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
    }
}
