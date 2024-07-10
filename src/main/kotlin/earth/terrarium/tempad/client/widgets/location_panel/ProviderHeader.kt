package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.olympus.client.components.lists.EntryListWidget
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.client.widgets.ModWidgets.CHEVRON_DOWN
import earth.terrarium.tempad.client.widgets.ModWidgets.CHEVRON_UP
import earth.terrarium.tempad.common.utils.sprites
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import java.util.function.Consumer

class ProviderHeader(val settings: ProviderSettings, val parent: PanelWidget): KListWidgetItem {
    companion object {
        val valueCaches = mutableMapOf<ResourceLocation, Boolean>()
    }

    val text = Component.translatable(settings.id.toLanguageKey("provider")).withStyle(ChatFormatting.UNDERLINE)
    var hidden
        get() = valueCaches.getOrPut(settings.id) { false }
        set(value) {
            valueCaches[settings.id] = value
        }

    override var _x: Int = 0
    override var _y: Int = 0
    override var _focused: Boolean = false
    override var _width: Int = 0
    override var _height: Int = 12

    override fun setFocused(pFocused: Boolean) {
        _focused = pFocused
    }

    override fun isFocused(): Boolean = _focused

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, pPartialTick: Float) {
        graphics.drawScrollingString(Minecraft.getInstance().font, text, _x + 4, _x + _width - 14, _y + 2, Tempad.ORANGE.value)

        val isHovered = mouseX >= _x + _width - 12 && mouseX <= _x + _width && mouseY >= _y && mouseY <= _y + 10
        if (hidden) {
            graphics.blitSprite(CHEVRON_UP.get(true, isHovered), _x + _width - 12, _y + 2, 10, 10)
        } else {
            graphics.blitSprite(CHEVRON_DOWN.get(true, isHovered), _x + _width - 12, _y + 2, 10, 10)
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        hidden = !hidden
        parent.update()
        return true
    }
}