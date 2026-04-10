package earth.terrarium.tempad.client.widgets

import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.clientLevel
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.CraftingRecipe
import kotlin.jvm.optionals.getOrNull

class CraftingRecipeWidget(val recipe: CraftingRecipe) : BaseWidget() {
    val font = Minecraft.getInstance().font
    var age = 0

    companion object {
        fun create(id: ResourceLocation): CraftingRecipeWidget? {
            val recipe = clientLevel?.recipeManager?.byKey(id) ?: return null
            (recipe.getOrNull()?.value as? CraftingRecipe)?.let { craftingRecipe ->
                return CraftingRecipeWidget(craftingRecipe)
            }
            return null
        }
    }

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
        var tooltipChanged = false
        for (row in 0 until tableSize) {
            for (col in 0 until tableSize) {
                if (row * tableSize + col >= recipe.ingredients.size) break
                val ingredient = recipe.ingredients[row * tableSize + col]
                if (ingredient.items.size == 0) continue
                val stack = ingredient.items[(age / 30) % ingredient.items.size]
                val itemX = x + 3 + col * 18
                val itemY = y + 3 + row * 18
                graphics.renderItem(stack, itemX, itemY)
                if (mouseX > itemX - 1 && mouseX < itemX + 17 && mouseY > itemY - 1 && mouseY < itemY + 17) {
                    tooltipChanged = true
                    tooltip = Tooltip.create(stack.hoverName)
                }
            }
        }
        graphics.fill(x + 58, y + 25, x + 90 - 20, y + 32, Tempad.ORANGE.value)
        graphics.blitSprite(TempadUI.element.get(true, false), x + 90 - 20, y + 18, 20, 20)
        val resultItem = recipe.getResultItem(clientLevel!!.registryAccess())
        graphics.renderItem(resultItem, x + 90 - 18, y + 20)
        graphics.renderItemDecorations(font, resultItem, x + 90 - 18, y + 20)
        if (mouseX > x + 90 - 18 && mouseX < x + width && mouseY > y + 20 && mouseY < y + 38) {
            tooltip = Tooltip.create(resultItem.hoverName)
        } else if (!tooltipChanged) {
            tooltip = null
        }
    }

    fun tick() {
        age++
    }
}