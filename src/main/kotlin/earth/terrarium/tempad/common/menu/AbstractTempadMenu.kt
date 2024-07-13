package earth.terrarium.tempad.common.menu

import earth.terrarium.tempad.common.apps.AppContent
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.jvm.optionals.getOrNull

open class AbstractTempadMenu<T: AppContent<T>>(id: Int, inventory: Inventory, type: MenuType<*>?, val appContent: T?) : AbstractContainerMenu(type, id) {
    init {
        this.addPlayerInvSlots(inventory)
    }

    val ctx = appContent!!.ctx
    val stack by ctx.stackDelegate(inventory.player)

    constructor(id: Int, inventory: Inventory, type: MenuType<*>?, locations: Optional<T>) : this(id, inventory, type, locations.getOrNull())

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasItem()) {
            val slotItem = slot.item
            itemStack = slotItem.copy()

            if (slotItem.isEmpty) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }
        return itemStack
    }

    override fun stillValid(pPlayer: Player): Boolean = true

    override fun moveItemStackTo(stack: ItemStack, startIndex: Int, endIndex: Int, reverseDirection: Boolean): Boolean {
        var itemStack: ItemStack
        var slot: Slot
        var bl = false
        var i = startIndex
        if (reverseDirection) {
            i = endIndex - 1
        }
        if (stack.isStackable) {
            while (!stack.isEmpty && (if (reverseDirection) i >= startIndex else i < endIndex)) {
                slot = slots[i]
                itemStack = slot.item
                if (!itemStack.isEmpty && ItemStack.isSameItemSameComponents(stack, itemStack)) {
                    val j = itemStack.count + stack.count
                    if (j <= slot.maxStackSize) {
                        stack.count = 0
                        itemStack.count = j
                        slot.setChanged()
                        bl = true
                    } else if (itemStack.count < slot.maxStackSize) {
                        stack.shrink(stack.maxStackSize - itemStack.count)
                        itemStack.count = slot.maxStackSize
                        slot.setChanged()
                        bl = true
                    }
                }
                if (reverseDirection) {
                    --i
                    continue
                }
                ++i
            }
        }
        if (!stack.isEmpty) {
            i = if (reverseDirection) endIndex - 1 else startIndex
            while (if (reverseDirection) i >= startIndex else i < endIndex) {
                slot = slots[i]
                itemStack = slot.item
                if (itemStack.isEmpty && slot.mayPlace(stack)) {
                    if (stack.count > slot.maxStackSize) {
                        slot.setByPlayer(stack.split(slot.maxStackSize))
                    } else {
                        slot.setByPlayer(stack.split(stack.count))
                    }
                    slot.setChanged()
                    bl = true
                    break
                }
                if (reverseDirection) {
                    --i
                    continue
                }
                ++i
            }
        }
        return bl
    }

    private fun addPlayerInvSlots(inventory: Inventory, x: Int = 48, y: Int = 157) {
        for (row in 0..2) {
            for (column in 0..8) {
                this.addSlot(makeInvSlot(inventory, column + row * 9 + 9 /* Hotbar is the first 9 */, x + column * 18, y + row * 18))
            }
        }

        for (k in 0..8) {
            this.addSlot(makeInvSlot(inventory, k, x + k * 18, y + 18 * 3 + 4))
        }
    }

    private fun makeInvSlot(inventory: Inventory, slotIndex: Int, x: Int, y: Int): Slot {
        return LockedSlot(inventory, slotIndex, x, y)
    }

    inner class LockedSlot(val inventory: Inventory, slotIndex: Int, x: Int, y: Int) : Slot(inventory, slotIndex, x, y) {
        override fun mayPlace(stack: ItemStack): Boolean = ctx.isLocked(this, inventory.player)
        override fun mayPickup(pPlayer: Player): Boolean = ctx.isLocked(this, inventory.player)
    }
}
