package me.codexadrian.tempad.common.menu;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.registry.TempadMenus;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PrinterMenu extends AbstractContainerMenu {
    public static final int MAX_SLOTS = 2;

    public Container container;
    public Inventory playerInventory;
    public List<LocationData> locations;
    public BlockPos printerPos;

    public PrinterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, new SimpleContainer(2), LocationData.CODEC.listOf().decode(buffer), CodecUtils.BLOCK_POS.decode(buffer));
    }

    public PrinterMenu(int containerId, Inventory playerInventory, Container container, List<LocationData> locations, BlockPos pos) {
        super(TempadMenus.PRINTER.get(), containerId);
        this.container = container;
        this.playerInventory = playerInventory;
        this.locations = locations;
        this.printerPos = pos;

        this.addSlot(new CardSlot(this.container, 0, 17, 110));
        this.addSlot(new CardSlot(this.container, 1, 63, 110));
        this.addPlayerInvSlots(this.playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
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

    protected void addPlayerInvSlots(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 15 + j * 18, 168 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 15 + k * 18, 226));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static class CardSlot extends Slot {
        public CardSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack itemStack) {
            return itemStack.is(TempadRegistry.LOCATION_CARD.get());
        }
    }
}
