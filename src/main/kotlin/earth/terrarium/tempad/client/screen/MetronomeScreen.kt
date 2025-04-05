package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen
import earth.terrarium.tempad.common.menu.MetronomeMenu
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class MetronomeScreen(menu: MetronomeMenu, playerInventory: Inventory, title: Component) : AbstractContainerCursorScreen<MetronomeMenu>(menu,
    playerInventory, title
) {
    companion object {
        val sprite = "screen/metronome".tempadId
        val screenWidth = 211
        val screenHeight = 178
    }

    var top = 0
    var left = 0

    override fun init() {
        super.init()
        left = (width - screenWidth) / 2
        top = (height - screenHeight) / 2
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blitSprite(sprite, left, top, screenWidth, screenHeight)
    }
}