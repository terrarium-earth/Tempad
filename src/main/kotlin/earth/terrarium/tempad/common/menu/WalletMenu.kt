package earth.terrarium.tempad.common.menu

import earth.terrarium.tempad.common.items.WalletInventory
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.get
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.SlotItemHandler

class WalletMenu(containerId: Int, inventory: Inventory, items: WalletInventory) : AbstractContainerMenu(ModMenus.wallet, containerId) {
    constructor(containerId: Int, inventory: Inventory) : this(containerId, inventory, WalletInventory(ItemStack.EMPTY))

    init {
        this.addMenuSlots(items)
        this.addPlayerInvSlots(inventory)
    }

    override fun quickMoveStack(
        player: Player,
        index: Int,
    ): ItemStack {
        var newStack = ItemStack.EMPTY
        val slot = this.slots[index]
        if (slot.hasItem()) {
            val originalStack = slot.item
            newStack = originalStack.copy()
            if (index < 18) {
                if (!this.moveItemStackTo(originalStack, 18, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(originalStack, 0, 18, false)) {
                return ItemStack.EMPTY
            }

            if (originalStack.isEmpty) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }
        return newStack
    }

    override fun stillValid(player: Player): Boolean = true

    private fun addPlayerInvSlots(inventory: Inventory, x: Int = 8, y: Int = 71) {
        for (row in 0..2) {
            for (column in 0..8) {
                this.addSlot(LockedSlot(inventory, column + row * 9 + 9 /* Hotbar is the first 9 */, x + column * 18, y + row * 18))
            }
        }

        for (k in 0..8) {
            this.addSlot(LockedSlot(inventory, k, x + k * 18, y + 18 * 3 + 4))
        }
    }

    private fun addMenuSlots(items: ItemStackHandler, x: Int = 8, y: Int = 19) {
        for (row in 0..1) {
            for (column in 0..8) {
                this.addSlot(SlotItemHandler(items, column + row * 9, x + column * 18, y + row * 18))
            }
        }
    }

    inner class LockedSlot(val inventory: Inventory, slotIndex: Int, x: Int, y: Int) : Slot(inventory, slotIndex, x, y) {
        override fun mayPickup(pPlayer: Player): Boolean = inventory[slotIndex].item !== ModItems.cardWallet
    }
}