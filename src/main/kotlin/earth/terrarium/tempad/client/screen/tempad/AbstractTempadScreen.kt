package earth.terrarium.tempad.client.screen.tempad

import com.mojang.blaze3d.systems.RenderSystem
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils
import earth.terrarium.olympus.client.components.textbox.TextBox
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.widgets.buttons.AppButton
import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import earth.terrarium.tempad.common.network.c2s.RedirectAppPacket
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.lwjgl.glfw.GLFW

abstract class AbstractTempadScreen<T : AbstractTempadMenu<*>>(
    val appSprite: ResourceLocation?,
    menu: T,
    val inv: Inventory,
    title: Component,
) : AbstractContainerCursorScreen<T>(menu, inv, title) {
    companion object {
        val background = "screen/tempad".tempadId
    }

    init {
        this.imageWidth = 256
        this.imageHeight = 256
        this.titleLabelX = 34
        this.titleLabelY = 27
    }

    var localLeft: Int = 0
    var localTop: Int = 0


    override fun init() {
        super.init()
        this.localTop = this.topPos + 20
        this.localLeft = this.leftPos + 30

        val appList = SelectionList<AppButton>(localLeft - 16, localTop + 1, 16, 116, 15) { button ->
            RedirectAppPacket(button!!.appId, menu.ctxHolder, menu.appContent.isStationary).sendToServer()
        }

        for ((id, app) in AppRegistry.getAll(
            menu.ctxHolder.getCtx(minecraft!!.player!!),
            menu.appContent.isStationary
        )) {
            appList.addEntry(AppButton(app, id))
        }

        addRenderableWidget(appList)
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blitSprite(background, this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        appSprite?.let {
            RenderSystem.enableBlend()
            graphics.blitSprite(it, this.leftPos + 30, this.topPos + 20, 198, 118)
        }
        menu.ctx.stack.chronons?.let {
            val height = ((it.power.toFloat() / it.maxPower) * 54).toInt()
            graphics.blitSprite(
                TempadUI.powerVert,
                6,
                54,
                0,
                54 - height,
                localLeft + 207,
                localTop + 32 + 54 - height,
                6,
                height
            )

            if (mouseX >= localLeft + 207 && mouseX <= localLeft + 211 && mouseY >= localTop + 32 && mouseY <= localTop + 86) {
                ScreenUtils.setTooltip(Component.literal("${it.power}/${it.maxPower}"))
            }
        }
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        (focused as? TextBox)?.let {
            if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
                focused = null
                return true
            } else if ((!it.keyPressed(
                    pKeyCode,
                    pScanCode,
                    pModifiers
                ) && !it.isFocused && !it.isActive && !it.isVisible) || pKeyCode == GLFW.GLFW_KEY_TAB
            ) {
                return super.keyPressed(pKeyCode, pScanCode, pModifiers)
            } else {
                return true
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }

    override fun renderLabels(graphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, Tempad.ORANGE.value, true)
        minecraft?.level?.dayTime?.let {
            val time = it % 24000
            val minutes = ((time % 1000) * 0.06).toInt()
            val hours = ((time / 1000) + 6) % 24
            val text = "${(hours).toString().padStart(2, '0')}:${(minutes).toString().padStart(2, '0')}"

            graphics.drawString(
                font,
                text,
                30 + 194 - font.width(text),
                this.titleLabelY,
                Tempad.ORANGE.value,
                true
            )
        }
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }
}