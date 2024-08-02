package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.common.registries.ModFluids
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.utils.minus
import earth.terrarium.tempad.common.utils.plus
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.SimpleFluidContent
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class ChrononGenerator : Item(Properties().stacksTo(1)) {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity.tickCount % 24 != 0) return // 1 mb every 1.2 seconds, 1 bucket per day

        ChrononContainer(stack) += 1
    }
}

class ChrononContainer(val stack: ItemStack) : IFluidHandlerItem {
    var content: Int by stack::chrononContent

    override fun getTanks(): Int = 1
    override fun getContainer(): ItemStack = stack
    override fun getFluidInTank(tank: Int): FluidStack = FluidStack(ModFluids.chronon, content)
    override fun getTankCapacity(tank: Int): Int = 4000
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = stack.fluid == ModFluids.chronon

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        return if (content == 0 || resource.fluid == ModFluids.chronon) {
            val amount = resource.amount.coerceAtMost(getTankCapacity(0) - content)
            if (action.execute()) content += amount
            amount
        } else 0
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return if (content == 0 && resource.`is`(ModFluids.chronon)) drain(resource.amount, action) else FluidStack.EMPTY
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        return if (content <= 0) FluidStack.EMPTY else FluidStack(ModFluids.chronon, content).also {
            it.amount = maxDrain.coerceAtMost(content)
            if (action.execute()) content -= it.amount
        }
    }

    operator fun plusAssign(amount: Int) {
        content += amount
    }

    operator fun minusAssign(amount: Int) {
        content -= amount
    }
}