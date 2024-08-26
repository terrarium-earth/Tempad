package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.common.registries.ModFluids
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.utils.get
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.attachment.AttachmentHolder
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

open class ItemChrononContainer(val stack: ItemStack, capacity: Int) : ChrononContainer(capacity), IFluidHandlerItem {
    override var content: Int by stack::chrononContent
    override fun getContainer(): ItemStack = stack
}

open class AttachmentChrononContainer(val attachmentHolder: AttachmentHolder, capacity: Int) : ChrononContainer(capacity) {
    override var content: Int by attachmentHolder::chrononContent
}

abstract class ChrononContainer(open val capacity: Int) : IFluidHandler {
    abstract var content: Int
    val hasRoom get() = content < capacity

    override fun getTanks(): Int = 1
    override fun getFluidInTank(tank: Int): FluidStack = FluidStack(ModFluids.stillChronon, content)
    override fun getTankCapacity(tank: Int): Int = capacity
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = stack.fluid == ModFluids.stillChronon

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        return if (content == 0 || resource.fluid == ModFluids.stillChronon) {
            val amount = resource.amount.coerceAtMost(getTankCapacity(0) - content)
            if (action.execute()) this += amount
            amount
        } else 0
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return if (content > 0 && resource.`is`(ModFluids.stillChronon)) drain(
            resource.amount,
            action
        ) else FluidStack.EMPTY
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        val amount = maxDrain.coerceAtMost(content)
        if (action.execute()) this -= amount
        return FluidStack(ModFluids.stillChronon, amount)
    }

    operator fun plusAssign(amount: Int) {
        content = Mth.clamp(content + amount, 0, capacity)
    }

    operator fun minusAssign(amount: Int) {
        content = Mth.clamp(content - amount, 0, capacity)
    }
}

val ItemStack.chrononContainer get() = this[Capabilities.FluidHandler.ITEM] as ChrononContainer
val BlockEntity.chrononContainer get() = level!!.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, Direction.UP) as? ChrononContainer

fun move(from: IFluidHandler, to: IFluidHandler, amount: Int) {
    var extracted = from.drain(amount, IFluidHandler.FluidAction.SIMULATE)
    var inserted = to.fill(extracted, IFluidHandler.FluidAction.SIMULATE)
    if (extracted.amount > 0 && inserted > 0) {
        while (extracted.amount != inserted) {
            extracted = from.drain(inserted, IFluidHandler.FluidAction.SIMULATE)
            inserted = to.fill(extracted, IFluidHandler.FluidAction.SIMULATE)
            if (extracted.amount == 0 || inserted == 0) {
                return;
            }
        }
        val drained = from.drain(extracted, IFluidHandler.FluidAction.EXECUTE)
        to.fill(extracted, IFluidHandler.FluidAction.EXECUTE)
    }
}