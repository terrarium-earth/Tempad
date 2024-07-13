package earth.terrarium.tempad.api.context.impl

import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.context.ContextType
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.common.items.TempadItem
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class InventoryItemContext(val slot: Int) : ItemContext {
    override val type: ContextType<*> = Companion.type

    override fun getStack(player: Player): ItemStack {
        return player.inventory.getItem(slot)
    }

    override fun setStack(player: Player, stack: ItemStack) {
        player.inventory.setItem(slot, stack)
    }

    override fun isLocked(slot: Slot, player: Player): Boolean {
        return slot.container === player.inventory && slot.index == this.slot
    }

    companion object {
        val id = "inventory".tempadId
        val codec = ByteCodec.INT.map(::InventoryItemContext, InventoryItemContext::slot)!!

        val type = ContextType(id, 0, codec) ctx@{
            for (i in 0 until it.inventory.containerSize) {
                val stack = it.inventory.getItem(i)
                if (stack.item is TempadItem) {
                    return@ctx InventoryItemContext(i)
                }
            }
            null
        }
    }
}

