package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.common.menu.WalletMenu
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.registries.walletContents
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.neoforged.neoforge.items.ItemStackHandler

class WalletInventory(val stack: ItemStack): ItemStackHandler(18), MenuProvider {
    val isFull: Boolean get() {
        for (item in stacks) {
            if (item.isEmpty) return false
        }
        return true
    }

    val hasAnyItems: Boolean get() {
        for (item in stacks) {
            if (!item.isEmpty) return true
        }
        return false
    }

    fun insertItem(stack: ItemStack, simulate: Boolean): ItemStack {
        var remaining = stack
        for (i in 0 until 18) {
            remaining = insertItem(i, remaining, simulate)
            if (remaining.isEmpty) return ItemStack.EMPTY
        }
        return remaining
    }

    init {
        stack.walletContents.copyInto(this.stacks)
    }

    override fun onContentsChanged(slot: Int) {
        stack.walletContents = ItemContainerContents.fromItems(stacks)
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return stack.portalTarget != null && stack.item === ModItems.locationCard
    }

    override fun getDisplayName(): Component {
        return stack.hoverName
    }

    override fun createMenu(
        containerId: Int,
        playerInventory: Inventory,
        player: Player,
    ): AbstractContainerMenu? {
        return WalletMenu(containerId, playerInventory, this)
    }
}