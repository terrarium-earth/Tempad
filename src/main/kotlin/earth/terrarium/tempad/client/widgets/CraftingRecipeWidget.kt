package earth.terrarium.tempad.client.widgets

import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.clientLevel
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.NormalCraftingRecipe
import net.minecraft.world.item.crafting.display.SlotDisplayContext
import kotlin.jvm.optionals.getOrNull

class CraftingRecipeWidget(val recipe: NormalCraftingRecipe) : BaseWidget() {
    val font = Minecraft.getInstance().font
    var age = 0

    companion object {
        fun create(id: Identifier): CraftingRecipeWidget? = Tempad.clientRecipes[id]?.let { CraftingRecipeWidget(it) }
    }

    init {
        height = 58
        width = 90
    }

    override fun extractWidgetRenderState(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TempadUI.element.get(true, false), x, y, 58, height)
        val tableSize = 3
        var tooltipChanged = false
        val indices = recipe.placementInfo().slotsToIngredientIndex()
        for (row in 0 until tableSize) {
            for (col in 0 until tableSize) {
                if (row * tableSize + col !in indices) break
                val ingredient = recipe.placementInfo().ingredients()[indices.indexOf(row * tableSize + col)]
                val ingredientItems =
                    ingredient.display().resolveForStacks(SlotDisplayContext.fromLevel(clientLevel!!))
                if (ingredientItems.isEmpty()) continue
                val stack = ingredientItems[(age / 30) % ingredientItems.size]
                val itemX = x + 3 + col * 18
                val itemY = y + 3 + row * 18
                graphics.item(stack, itemX, itemY)
                if (mouseX > itemX - 1 && mouseX < itemX + 17 && mouseY > itemY - 1 && mouseY < itemY + 17) {
                    tooltipChanged = true
                    setTooltip(Tooltip.create(stack.hoverName))
                }
            }
        }
        graphics.fill(x + 58, y + 25, x + width - 20, y + 32, Tempad.ORANGE.value)
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TempadUI.element.get(true, false), x + width - 20, y + 18, 20, 20)
        val resultItem = recipe.assemble(CraftingInput.EMPTY)
        graphics.item(resultItem, x + width - 18, y + 20)
        graphics.itemDecorations(font, resultItem, x + width - 18, y + 20)
        if (mouseX > x + width - 18 && mouseX < x + width && mouseY > y + 20 && mouseY < y + 38) {
            setTooltip(Tooltip.create(resultItem.hoverName))
        } else if (!tooltipChanged) {
            setTooltip(null)
        }
    }

    fun tick() {
        age++
    }
}