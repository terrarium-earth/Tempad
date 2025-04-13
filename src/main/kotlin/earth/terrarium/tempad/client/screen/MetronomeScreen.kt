package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.common.menu.MetronomeMenu
import earth.terrarium.tempad.common.network.c2s.UpdateMetronomePacket
import earth.terrarium.tempad.common.registries.metronomeEnergy
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class MetronomeScreen(menu: MetronomeMenu, playerInventory: Inventory, title: Component) : AbstractContainerCursorScreen<MetronomeMenu>(menu,
    playerInventory, title
) {
    companion object {
        val sprite = "screen/metronome".tempadId
    }

    val locked = MutableState.of(menu.data?.locked == true)

    init {
        this.imageWidth = 211
        this.imageHeight = 178
    }

    override fun init() {
        super.init()

        this.titleLabelX = 25
        this.titleLabelY = 22
        this.inventoryLabelX = 25

        addRenderableWidget(Widgets.button {
            it.withSize(11)
            it.withPosition(leftPos + 161, topPos + 19)
            it.withTexture(TempadUI.steelButton)
            it.withRenderer(locked.withRenderer {
                WidgetRenderers.icon<Button>(if (it) TempadUI.lockIcon else TempadUI.unlockIcon).withColor(MinecraftColors.BLACK).withCentered(7, 7)
            })
            it.withCallback {
                locked.value = !locked.value
            }
        })

        addRenderableWidget(Widgets.button {
            it.withSize(11)
            it.withPosition(leftPos + 174, topPos + 19)
            it.withTexture(TempadUI.steelButton)
            it.withRenderer(WidgetRenderers.icon<Button>(TempadUI.xIcon).withColor(MinecraftColors.BLACK).withCentered(7, 7))
            it.withCallback(::onClose)
        })
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blitSprite(sprite, leftPos, topPos, imageWidth, imageHeight)

        safeLet(metronomeEnergy, menu.data?.uuid) { energy, id ->
            val power = energy.getStored(id)
            val capacity = energy.getCapacity(id)
            val height = ((power.toFloat() / capacity).coerceIn(0f, 1f) * 54).toInt()
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

    override fun onClose() {
        super.onClose()
        UpdateMetronomePacket(menu.data!!.pos.pos, locked.value).sendToServer()
    }
}