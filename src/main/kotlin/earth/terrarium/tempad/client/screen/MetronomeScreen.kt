package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.common.menu.MetronomeMenu
import earth.terrarium.tempad.common.registries.metronomeEnergy
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class MetronomeScreen(menu: MetronomeMenu, playerInventory: Inventory, title: Component) : AbstractContainerCursorScreen<MetronomeMenu>(menu,
    playerInventory, title
) {
    companion object {
        val sprite = "screen/metronome".tempadId
    }

    init {
        this.imageWidth = 211
        this.imageHeight = 178
    }

    override fun init() {
        super.init()

        this.titleLabelX = 25
        this.titleLabelY = 22
        this.inventoryLabelX = 25
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blitSprite(sprite, leftPos, topPos, imageWidth, imageHeight)

        safeLet(metronomeEnergy, menu.data?.uuid) { energy, id ->
            val power = energy.getStored(id)
            val capacity = energy.getCapacity(id)
            val height = ((power.toFloat() / capacity) * 54).toInt()
            graphics.blitSprite(TempadUI.powerVert, 6, 54, 0, 54 - height, leftPos + 102, topPos + 20 + 54 - height, 6, height)

            if (mouseX >= leftPos + 102 && mouseX <= leftPos + 106 && mouseY >= topPos + 20 && mouseY <= topPos + 74) {
                ScreenUtils.setTooltip(Component.literal("${power}/${capacity}"))
            }
        }
    }

    override fun renderLabels(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
    ) {
        super.renderLabels(guiGraphics, mouseX, mouseY)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}