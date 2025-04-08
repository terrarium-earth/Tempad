package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen
import earth.terrarium.tempad.common.menu.WalletMenu
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot

class WalletScreen(menu: WalletMenu, inv: Inventory, component: Component) :
    AbstractContainerCursorScreen<WalletMenu>(menu, inv, component) {
    companion object {
        val sprite = "screen/wallet".tempadId
        val slot = "screen/wallet_slot".tempadId
    }

    init {
        imageHeight = 154
        inventoryLabelY = 58
    }

    override fun renderBg(
        guiGraphics: GuiGraphics,
        partialTick: Float,
        mouseX: Int,
        mouseY: Int,
    ) {
        guiGraphics.blitSprite(sprite, leftPos, topPos, imageWidth, imageHeight)
        val x = leftPos + 7
        val y = topPos + 18
        for (row in 0 .. 1) {
            for (column in 0..8) {
                guiGraphics.blitSprite(slot, x + column * 18, y + row * 18, 18, 18)
            }
        }

        for (row in 0..2) {
            for (column in 0..8) {
                guiGraphics.blitSprite(slot, x + column * 18, y + row * 18 + 52, 18, 18)
            }
        }

        for (k in 0..8) {
            guiGraphics.blitSprite(slot, x + k * 18, y + 18 * 3 + 56, 18, 18)
        }
    }

    override fun render(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        f: Float,
    ) {
        super.render(graphics, mouseX, mouseY, f)
        renderTooltip(graphics, mouseX, mouseY)
    }
}