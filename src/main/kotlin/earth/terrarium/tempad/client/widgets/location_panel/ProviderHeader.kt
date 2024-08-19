package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.widgets.KListWidgetItem
import earth.terrarium.tempad.client.widgets.ModWidgets.CHEVRON_DOWN
import earth.terrarium.tempad.client.widgets.ModWidgets.CHEVRON_UP
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

data class ProviderHeader(val text: Component, val id: String, val parent: PanelWidget): KListWidgetItem, Comparable<ProviderHeader> {
    companion object {
        val valueCaches = mutableMapOf<String, Boolean>()
    }

    constructor(settings: ResourceLocation, parent: PanelWidget): this(Component.translatable(settings.toLanguageKey("provider")), settings.toLanguageKey(), parent)

    var hidden = valueCaches.getOrPut(id) { false }
        set(value) {
            valueCaches[id] = value
            field = value
        }

    override var _x: Int = 0
    override var _y: Int = 0
    override var _focused: Boolean = false
    override var _width: Int = 0
    override var _height: Int = 14

    override fun setFocused(pFocused: Boolean) {
        _focused = pFocused
    }

    override fun isFocused(): Boolean = _focused

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, pPartialTick: Float) {
        val isHovered = mouseX > _x && mouseX < _x + _width && mouseY > _y && mouseY < _y + height
        val color = if (isHovered) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value
        graphics.drawScrollingString(Minecraft.getInstance().font, text, _x + 4, _x + _width - 14, _y + 2, color)

        val texture = if (hidden) CHEVRON_UP else CHEVRON_DOWN
        graphics.blitSprite(texture.get(true, isHovered), _x + _width - 10, _y + 3, 6, 6)
        graphics.hLine(_x + 3, _x + _width - 3, _y + _height - 3, color)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        hidden = !hidden
        parent.update()
        return true
    }

    override fun compareTo(other: ProviderHeader): Int {
        return text.string.compareTo(other.text.string)
    }
}