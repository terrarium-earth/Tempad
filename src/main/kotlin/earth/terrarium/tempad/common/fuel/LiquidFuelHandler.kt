package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.recipe.SingleFluidRecipeInput
import earth.terrarium.tempad.common.registries.ModRecipes
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.utils.contains
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class LiquidFuelHandler(tempadStack: ItemStack, override val totalCharges: Int): BaseFuelHandler(tempadStack), IFluidHandlerItem {
    override fun getTanks(): Int = 1

    override fun getFluidInTank(tank: Int): FluidStack = FluidStack.EMPTY

    override fun getTankCapacity(tank: Int): Int = Int.MAX_VALUE

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = stack in ModTags.LIQUID_FUEL

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        val input = SingleFluidRecipeInput(resource)
        val recipe = Tempad.server!!.recipeManager.getAllRecipesFor(ModRecipes.LIQUID_FUEL_TYPE)
            .find { it.value().matches(input) }
            ?: return 0

        if (resource.isEmpty || charges >= totalCharges) return 0

        val amount = (resource.amount / recipe.value().amount).coerceAtMost(totalCharges - charges)
        if (amount == 0) return 0
        if (action.execute()) charges += amount
        return amount * recipe.value().amount
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY

    override fun getContainer(): ItemStack = stack
}