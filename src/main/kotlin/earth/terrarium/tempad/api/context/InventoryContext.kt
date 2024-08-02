package earth.terrarium.tempad.api.context

import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.utils.slot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class InventoryContext(val player: Player, override val data: Int): SyncableContext<Int> {
    override val type: ContextType<Int> = Companion.type

    override var stack: ItemStack by player.inventory.slot(data)

    override fun addStack(stack: ItemStack) {
        player.inventory.placeItemBackInInventory(stack)
    }

    companion object {
        val type = ContextType("inventory".tempadId, ByteCodec.INT)
    }
}