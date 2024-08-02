package earth.terrarium.tempad.client.widgets

import earth.terrarium.tempad.Tempad
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component

class LabeledWidget(title: Component, val subtitle: Component,  val labelMargin: Int, val widget: AbstractWidget, val labelWidth: Int = 94, val ignoreResize: Boolean = false) :
    KListWidgetItem {
    val titleWidget = StringWidget(title, Minecraft.getInstance().font).setColor(Tempad.ORANGE.value)
    val tooltip = listOf(title.visualOrderText) + Minecraft.getInstance().font.split(subtitle, 125)

    override var _x: Int = 0
        set(value) {
            field = value
            titleWidget.x = value + 1
            widget.x = value + width - widget.width
        }
    override var _y: Int = 0
        set(value) {
            field = value
            titleWidget.y = value + labelMargin
            widget.y = value
        }
    override var _focused: Boolean = false
    override var _width: Int = 0
        set(value) {
            field = value
            if (!ignoreResize) widget.width = value - labelWidth - 2
        }
    override var _height: Int = widget.height

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        titleWidget.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        widget.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        if (titleWidget.isHovered) {
            Minecraft.getInstance().screen?.setTooltipForNextRenderPass(tooltip)
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        return widget.mouseClicked(pMouseX, pMouseY, pButton)
    }
}
