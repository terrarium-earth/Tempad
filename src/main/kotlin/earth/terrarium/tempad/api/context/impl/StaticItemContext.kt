package earth.terrarium.tempad.api.context.impl

import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.context.ContextType
import earth.terrarium.tempad.api.context.ItemContext
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class StaticItemContext(val stack: ItemStack): ItemContext {
    override val type: ContextType<*> = Companion.type

    override fun getStack(player: Player): ItemStack = stack

    override fun setStack(player: Player, stack: ItemStack) {}

    override fun isLocked(slot: Slot, player: Player): Boolean = false

    companion object {
        val id = "static".tempadId
        val codec = ExtraByteCodecs.ITEM_STACK.map(::StaticItemContext, StaticItemContext::stack)
        val type = ContextType(id, Int.MIN_VALUE, codec)
    }
}