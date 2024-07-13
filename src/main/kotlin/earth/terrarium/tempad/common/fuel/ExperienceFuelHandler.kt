package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.api.context.ContextInstance
import earth.terrarium.tempad.common.registries.experienceConsumeAmount
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class ExperienceFuelHandler(stack: ItemStack, override val totalCharges: Int) : BaseFuelHandler(stack, "experience"),
    IFluidHandlerItem {
    override fun getTanks(): Int = 1

    override fun getFluidInTank(tank: Int): FluidStack = FluidStack.EMPTY

    override fun getTankCapacity(tank: Int): Int = 1000

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = false

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int =
        0 // TODO add support for modded experience fluids

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY

    override fun getContainer(): ItemStack = stack

    override fun addChargeFromPlayer(player: Player): Boolean {
        val toConsume = stack.experienceConsumeAmount
        if (player.totalExperience >= toConsume) {
            player.totalExperience -= toConsume
            this += 1
            return true
        }
        return false
    }

    override fun addChargeFromItem(context: ContextInstance): Boolean {
        return false // TODO add support for modded experience containers
    }
}