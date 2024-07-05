package earth.terrarium.tempad.common.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.fluids.FluidStack

data class SingleFluidRecipeInput(val stack: FluidStack): RecipeInput {
    override fun getItem(p_346128_: Int): ItemStack = ItemStack.EMPTY
    override fun size(): Int = 1
}
