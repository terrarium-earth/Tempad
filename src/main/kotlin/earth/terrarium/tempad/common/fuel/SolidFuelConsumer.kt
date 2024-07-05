package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModRecipes
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.utils.contains
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.neoforged.neoforge.items.IItemHandler

class SolidFuelConsumer(tempadStack: ItemStack, override val totalCharges: Int) : BaseFuelConsumer(tempadStack), IItemHandler {

    override fun getSlots(): Int = 1

    override fun getStackInSlot(slot: Int): ItemStack = ItemStack.EMPTY

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        val input = SingleRecipeInput(stack)
        val recipe = Tempad.server!!.recipeManager.getAllRecipesFor(ModRecipes.SOLID_FUEL_TYPE)
            .find { it.value().matches(input) }
            ?: return stack

        if (stack.isEmpty || charges >= totalCharges) return stack

        val amount = (stack.count / recipe.value().count).coerceAtMost(totalCharges - charges)
        if (amount == 0) return stack
        if (!simulate) charges += amount
        val toShrink = amount * recipe.value().count
        return if (stack.count == toShrink) ItemStack.EMPTY else stack.copy().apply { shrink(toShrink) }
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = ItemStack.EMPTY;

    override fun getSlotLimit(slot: Int): Int = Int.MAX_VALUE

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = stack in ModTags.ITEM_FUEL
}