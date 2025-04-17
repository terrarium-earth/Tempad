package earth.terrarium.tempad.client.widgets

import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.layouts.Layouts.row
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.clientLevel
import earth.terrarium.tempad.client.screen.guide.KnowledgeScreen
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.CraftingRecipe
import kotlin.jvm.optionals.getOrNull

class CraftingRecipeWidget(val screen: KnowledgeScreen, val recipe: CraftingRecipe): BaseWidget() {
    init {
        height = 58
        width = 90
    }

    override fun renderWidget(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {
        graphics.blitSprite(TempadUI.element.get(true, false), x, y, 58, height)
        val tableSize = if (recipe.ingredients.size > 4) 3 else 2
        for (row in 0 until tableSize ) {
            for (col in 0 until tableSize ) {
                if (row * tableSize + col >= recipe.ingredients.size) break
                val ingredient = recipe.ingredients[row * tableSize + col]
                if (ingredient.items.size == 0) continue
                val stack = ingredient.items[(screen.age / 60) % ingredient.items.size]
                graphics.renderItem(stack, x + 3 + col * 18, y + 3 + row * 18)
            }
        }
        graphics.fill(x + 58, y + 25, x + width - 20, y + 32, Tempad.ORANGE.value)
        graphics.blitSprite(TempadUI.element.get(true, false), x + width - 20, y + 18, 20, 20)
        graphics.renderItem(recipe.getResultItem(clientLevel!!.registryAccess()), x + width - 18, y + 20)
    }
}

fun KnowledgeScreen.recipe(id: ResourceLocation): CraftingRecipeWidget? {
    val recipe = clientLevel?.recipeManager?.byKey(id)
    (recipe?.getOrNull()?.value as? CraftingRecipe)?.let { craftingRecipe ->
        return CraftingRecipeWidget(this, craftingRecipe)
    }
    return null
}