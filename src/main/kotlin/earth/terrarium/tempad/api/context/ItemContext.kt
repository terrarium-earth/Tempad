package earth.terrarium.tempad.api.context

import earth.terrarium.tempad.common.registries.ModFluids
import earth.terrarium.tempad.common.utils.get
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

interface ItemContext {
    var stack: ItemStack

    fun exchange(toInsert: ItemStack) {
        val old = stack.copy()
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

fun ItemContext.drain(amount: Int): Boolean {
    val handler = stack[Capabilities.FluidHandler.ITEM] ?: return false
    val drained = handler.drain(FluidStack(ModFluids.stillChronon, amount), IFluidHandler.FluidAction.SIMULATE)
    if (drained.amount != amount) return false
    handler.drain(FluidStack(ModFluids.stillChronon, amount), IFluidHandler.FluidAction.EXECUTE)
    return true
}

fun Container.ctx(slot: Int, dropper: (ItemStack) -> Unit) = object: ItemContext {
    override var stack: ItemStack
        get() = getItem(slot)
        set(value) { setItem(slot, value) }

    override fun addStack(stack: ItemStack) {
        dropper(stack)
    }
}