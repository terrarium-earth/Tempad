package me.codexadrian.tempad.blocks;

import me.codexadrian.tempad.registry.TempadMenus;
import me.codexadrian.tempad.items.TempadItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TempadBlockMenu extends AbstractContainerMenu {
    public Container container;
    public BlockPos pos;

    protected TempadBlockMenu(Container container, int i, Inventory inventory, BlockPos pos) {
        super(TempadMenus.TEMPAD_BLOCK_MENU.get(), i);
        this.container = container;
        this.pos = pos;
        this.addSlot(new Slot(this.container, 0, 22, 22) {
            @Override
            public boolean mayPlace(@NotNull ItemStack itemStack) {
                return itemStack.getItem() instanceof TempadItem;
            }
        });
        for (int index = 1; index < 12; index++) {
            this.addSlot(new Slot(this.container, index, 22 + (index - 1) * 18, 112));
        }
        addPlayerInvSlots(inventory);
    }

    public TempadBlockMenu(int i, Inventory inventory, FriendlyByteBuf byteBuf) {
        this(new SimpleContainer(12), i, inventory, byteBuf.readBlockPos());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();

            if (index < 12) {
                if (!this.moveItemStackTo(slotItem, 12, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, 12, false)) {
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
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    protected void addPlayerInvSlots(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 40 + j * 18, 155 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 40 + k * 18, 155 + 58));
        }
    }
}
