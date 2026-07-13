package earth.terrarium.tempad.client.screen

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.common.menu.WalletMenu
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class WalletScreen(menu: WalletMenu, inv: Inventory, component: Component) : AbstractContainerScreen<WalletMenu>(menu, inv, component, 154, 166) {
    companion object {
        val sprite = "screen/wallet".tempadId
        val slot = "screen/wallet_slot".tempadId
    }

    init {
        inventoryLabelY = 58
    }

    override fun init() {
        super.init()

        addRenderableWidget(Widgets.button {
            it.withSize(7)
            it.withPosition(leftPos + imageWidth - 13, topPos + 6)
            it.withTexture(null)
            it.withRenderer(
                WidgetRenderers.withColors(
                    WidgetRenderers.icon(TempadUI.xIcon),
                    MinecraftColors.GRAY,
                    MinecraftColors.BLACK,
                    MinecraftColors.WHITE
                )
            )
            it.withCallback(::onClose)
        })
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, leftPos, topPos, imageWidth, imageHeight)
        val x = leftPos + 7
        val y = topPos + 18
        for (row in 0..1) {
            for (column in 0..8) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, slot, x + column * 18, y + row * 18, 18, 18)
            }
        }

        for (row in 0..2) {
            for (column in 0..8) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, slot, x + column * 18, y + row * 18 + 52, 18, 18)
            }
        }

        for (k in 0..8) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, slot, x + k * 18, y + 18 * 3 + 56, 18, 18)
        }
    }

    protected override fun extractLabels(graphics: GuiGraphicsExtractor, xm: Int, ym: Int) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF000000.toInt(), false)
        graphics.text(
            this.font,
            this.playerInventoryTitle,
            this.inventoryLabelX,
            this.inventoryLabelY,
            0xFF000000.toInt(),
            false
        )
    }
}