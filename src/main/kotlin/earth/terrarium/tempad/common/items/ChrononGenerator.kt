package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.common.registries.ModFluids
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.utils.contents
import earth.terrarium.tempad.common.utils.get
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class ChrononGenerator : Item(Properties().stacksTo(1)), ChrononAcceptor {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity.tickCount % 24 != 0) return // 1 mb every 1.2 seconds, 1 bucket per day
        if (entity !is Player || ContextRegistry.locate(entity) { it.item is ChrononGenerator && it !== stack } != null) return

        stack.chrononContainer += 1

        ContextRegistry.locate(entity) {
            it.item is ChrononAcceptor && it.chrononContainer.hasRoom && it !== stack
        }?.let {
            move(stack.chrononContainer, it.stack.chrononContainer, 1000)
        }
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action == ClickAction.SECONDARY && slot.hasItem() && slot.contents.item is ChrononAcceptor) {
            move(stack.chrononContainer, slot.contents.chrononContainer, 1000)
            return true
        }
        return super.overrideStackedOnOther(stack, slot, action, player)
    }
}

val ItemStack.chrononContainer get() = this[Capabilities.FluidHandler.ITEM] as ChrononContainer

open class ChrononContainer(val stack: ItemStack, open val capacity: Int) : IFluidHandlerItem {
    var content: Int by stack::chrononContent
    val hasRoom get() = content < capacity

    override fun getTanks(): Int = 1
    override fun getContainer(): ItemStack = stack
    override fun getFluidInTank(tank: Int): FluidStack = FluidStack(ModFluids.chronon, content)
    override fun getTankCapacity(tank: Int): Int = capacity
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = stack.fluid == ModFluids.chronon

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        return if (content == 0 || resource.fluid == ModFluids.chronon) {
            val amount = resource.amount.coerceAtMost(getTankCapacity(0) - content)
            if (action.execute()) content += amount
            amount
        } else 0
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return if (content == 0 && resource.`is`(ModFluids.chronon)) drain(
            resource.amount,
            action
        ) else FluidStack.EMPTY
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        return if (content <= 0) FluidStack.EMPTY else FluidStack(ModFluids.chronon, content).also {
            it.amount = maxDrain.coerceAtMost(content)
            if (action.execute()) content -= it.amount
        }
    }

    operator fun plusAssign(amount: Int) {
        content = (content + amount).coerceAtMost(capacity)
    }

    operator fun minusAssign(amount: Int) {
        content = (content - amount).coerceAtLeast(0)
    }
}

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
        from.drain(extracted, IFluidHandler.FluidAction.EXECUTE)
        to.fill(extracted, IFluidHandler.FluidAction.EXECUTE)
    }
}