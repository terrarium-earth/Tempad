package earth.terrarium.tempad.common.menu

import earth.terrarium.tempad.common.apps.AppContent
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.utils.get
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.*

open class AbstractTempadMenu<T: AppContent<T>>(id: Int, inventory: Inventory, type: MenuType<*>?, val appContent: T) : AbstractContainerMenu(type, id) {
    init {
        this.addPlayerInvSlots(inventory)
    }

    val ctxHolder = appContent.ctx
    val ctx = ctxHolder.getCtx(inventory.player)

    constructor(id: Int, inventory: Inventory, type: MenuType<*>?, locations: Optional<T>) : this(id, inventory, type, locations.orElseThrow())

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(pPlayer: Player): Boolean = true

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

    protected fun putBack(player: Player, stack: ItemStack) {
        if (player is ServerPlayer) {
            if (player.isAlive && !player.hasDisconnected()) {
                player.inventory.placeItemBackInInventory(stack)
            } else {
                player.drop(stack, false)
            }
        }
    }

    inner class LockedSlot(val inventory: Inventory, slotIndex: Int, x: Int, y: Int) : Slot(inventory, slotIndex, x, y) {
        override fun mayPickup(pPlayer: Player): Boolean = inventory[slotIndex].item !is TempadItem
    }
}
