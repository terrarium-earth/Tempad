package earth.terrarium.tempad.api.test

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

interface ItemContext {
    var stack: ItemStack

    fun exchange(toInsert: ItemStack) {
        val old = toInsert
        if (old.count > toInsert.count) {
            stack = old.also { it.count -= toInsert.count }
            addStack(toInsert)
        } else if (old.count == toInsert.count) {
            stack = toInsert
        } else {
            stack = toInsert.copy().also { it.count -= old.count }
        }
    }

    fun addStack(stack: ItemStack)
}

fun Container.ctx(slot: Int, dropper: (ItemStack) -> Unit) = object: ItemContext {
    override var stack: ItemStack
        get() = getItem(slot)
        set(value) { setItem(slot, value) }

    override fun addStack(stack: ItemStack) {
        dropper(stack)
    }
}