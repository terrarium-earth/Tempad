package earth.terrarium.tempad.client.screen.tempad

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.components.textbox.TextBox
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import earth.terrarium.tempad.common.network.c2s.RedirectAppPacket
import earth.terrarium.tempad.common.network.c2s.UpdateTempadLockPacket
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.utils.appSprites
import earth.terrarium.tempad.common.utils.appTitle
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory

abstract class AbstractTempadScreen<T : AbstractTempadMenu<*>>(
    val appSprite: Identifier?,
    menu: T,
    val inv: Inventory,
    title: Component,
) : AbstractContainerScreen<T>(menu, inv, title, 256, 256) {
    companion object {
        val background = "screen/tempad".tempadId
    }

    init {
        this.titleLabelX = 34
        this.titleLabelY = 27
    }

    var localLeft: Int = 0
    var localTop: Int = 0

    var timeText = ""
    var locked = MutableState.of(menu.ctx.resource.locked)

    override fun init() {
        super.init()
        this.localTop = this.topPos + 20
        this.localLeft = this.leftPos + 30

        val appList = Widgets.list {
            it.withSize(18, 118)
            it.withPosition(localLeft - 17, localTop)
            it.withTexture(TempadUI.element.get(true, false))
            it.withOverscrollY(1)
            it.withOverscrollX(1)
            it.withContentMargin(1)
            it.withContents {
                it.withGap(1)
                for ((id, app) in AppRegistry.getAll(
                    menu.ctxHolder,
                    menu.appContent.isStationary
                )) {
                    it.withChild(Widgets.button {
                        it.active = app.isEnabled(minecraft.player!!)
                        it.withSize(14, 14)
                        it.withTexture(TempadUI.element)
                        it.withRenderer(WidgetRenderers.sprite(id.appSprites()))
                        it.withTooltip(id.appTitle())
                        it.withCallback {
                            RedirectAppPacket(id, menu.ctxHolder, menu.appContent.isStationary).sendToServer()
                        }
                    })
                }
            }
        }

        addRenderableWidget(appList)

        addRenderableWidget(Widgets.button {
            it.withSize(11)
            it.withPosition(leftPos + 233, topPos + 13)
            it.withTexture(TempadUI.steelButton)
            it.withRenderer(
                WidgetRenderers.icon<Button>(TempadUI.xIcon).withColor(MinecraftColors.BLACK).withCentered(7, 7)
            )
            it.withCallback(::onClose)
        })
        if (menu.appContent.isStationary) {
            addRenderableWidget(Widgets.button {
                it.withSize(11)
                it.withPosition(leftPos + 233, topPos + 25)
                it.withTexture(TempadUI.steelButton)
                it.withRenderer(
                    locked.withRenderer {
                        WidgetRenderers.icon<Button>(if (it) TempadUI.lockIcon else TempadUI.unlockIcon)
                            .withColor(MinecraftColors.BLACK).withCentered(7, 7)
                    }
                )
                it.withCallback {
                    locked.value = !locked.value
                    UpdateTempadLockPacket(locked.value, menu.ctxHolder).sendToServer()
                }
            })
        }
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            background,
            this.leftPos,
            this.topPos,
            this.imageWidth,
            this.imageHeight
        )
        appSprite?.let {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, it, this.leftPos + 30, this.topPos + 20, 198, 118)
        }
        menu.ctx.chronons?.let {
            val height = ((it.amountAsInt.toFloat() / it.capacityAsInt).coerceIn(0f, 1f) * 54).toInt()
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
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
                graphics.setTooltipForNextFrame(
                    Component.literal("${it.amountAsInt}/${it.capacityAsInt}"),
                    mouseX,
                    mouseY
                )
            }
        }
    }

    override fun extractLabels(graphics: GuiGraphicsExtractor, xm: Int, ym: Int) {
        super.extractLabels(graphics, xm, ym)
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, Tempad.ORANGE.value, true)
        minecraft.level?.let { level ->
            val time = level.defaultClockTime % 24000
            val minutes = ((time % 1000) * 0.06).toInt()
            val hours = ((time / 1000) + 6) % 24
            timeText = "${(hours).toString().padStart(2, '0')}:${(minutes).toString().padStart(2, '0')}"

            graphics.text(
                this.font,
                timeText,
                30 + 194 - font.width(timeText),
                this.titleLabelY,
                Tempad.ORANGE.value,
                true
            )
        }
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        (focused as? TextBox)?.let {
            if (event.isEscape) {
                focused = null
                return true
            } else if ((!it.keyPressed(event) && !it.isFocused && !it.isActive && !it.isVisible) || event.input() == InputConstants.KEY_TAB) {
                return super.keyPressed(event)
            } else {
                return true
            }
        }
        return super.keyPressed(event)
    }
}