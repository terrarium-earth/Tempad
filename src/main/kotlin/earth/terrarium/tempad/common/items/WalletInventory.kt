package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.common.menu.WalletMenu
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.utils.commit
import earth.terrarium.tempad.common.utils.exchange
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.IndexModifier
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler
import net.neoforged.neoforge.transfer.item.ItemResource

val ItemStack.items: WalletInventory get() = WalletInventory(ItemAccess.forStack(this))

class WalletInventory(val stack: ItemAccess): ItemAccessItemHandler(stack, ModComponents.walletContents, 18), IndexModifier<ItemResource>, MenuProvider {
    val isFull: Boolean get() {
        for (i in 0 until size) {
            if (getAmountAsInt(i) == 0) return false
        }
        return true
    }

    override fun isValid(
        index: Int,
        resource: ItemResource,
    ): Boolean {
        val stack = resource.toStack()
        return stack.portalTarget != null && stack.item === ModItems.locationCard
    }

    override fun getDisplayName(): Component {
        return stack.resource.hoverName
    }

    override fun createMenu(
        containerId: Int,
        playerInventory: Inventory,
        player: Player,
    ): AbstractContainerMenu? {
        return WalletMenu(containerId, playerInventory, this)
    }

    override fun set(
        index: Int,
        resource: ItemResource,
        amount: Int,
    ) {
        transfer {
            itemAccess.exchange(update(itemAccess.resource, index, resource, amount)!!, itemAccess.amount)
            commit()
        }
    }
}