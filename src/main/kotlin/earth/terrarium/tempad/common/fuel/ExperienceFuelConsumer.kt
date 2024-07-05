package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.common.config.CommonConfigCache
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class ExperienceFuelConsumer(stack: ItemStack, override val totalCharges: Int) : BaseFuelConsumer(stack), IFluidHandlerItem {
    fun playerHasEnoughExp(player: Player) = player.totalExperience >= CommonConfigCache.expPerCharge

    fun takeExp(player: Player) {
        if (playerHasEnoughExp(player)) {
            player.totalExperience -= CommonConfigCache.expPerCharge
            this += 1
        }
    }

    override fun getTanks(): Int = 1

    override fun getFluidInTank(tank: Int): FluidStack = FluidStack.EMPTY

    override fun getTankCapacity(tank: Int): Int = 1000

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = false

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int = 0 // TODO add support for modded experience fluids

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY

    override fun getContainer(): ItemStack = stack
}