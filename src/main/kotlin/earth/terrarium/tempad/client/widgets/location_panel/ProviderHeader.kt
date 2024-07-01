package earth.terrarium.tempad.client.widgets.location_panel

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack
import earth.terrarium.olympus.client.components.lists.EntryListWidget
import earth.terrarium.olympus.client.components.lists.ListEntry
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.common.utils.sprites
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class ProviderHeader(val settings: ProviderSettings): ListEntry<Any> {
    companion object {
        val valueCaches = mutableMapOf<ResourceLocation, Boolean>()

        val CHEVRON_DOWN = "chevron_down".sprites("misc")
        val CHEVRON_UP = "chevron_up".sprites("misc")
    }

    val text = Component.translatable(settings.id.toLanguageKey("provider")).withStyle(ChatFormatting.UNDERLINE)
    var hidden
        get() = valueCaches.getOrPut(settings.id) { false }
        set(value) {
            valueCaches[settings.id] = value
        }

    var parent: EntryListWidget<Any>? = null

    override fun render(
        graphics: GuiGraphics,
        scissor: ScissorBoxStack,
        x: Int,
        y: Int,
        width: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        partialTick: Float
    ) {
        graphics.drawScrollingString(Minecraft.getInstance().font, text, x + 2, x + width - 14, y + 2, Tempad.ORANGE.value)

        val isHovered = hovered && mouseX >= x + width - 12 && mouseX <= x + width && mouseY >= y && mouseY <= y + 10
        if (hidden) {
            graphics.blitSprite(CHEVRON_UP.get(true, isHovered), x + width - 12, y, 10, 10)
        } else {
            graphics.blitSprite(CHEVRON_DOWN.get(true, isHovered), x + width - 12, y, 10, 10)
        }
    }

    override fun setList(list: EntryListWidget<Any>?) {
        parent = list
    }

    override fun getHeight(width: Int): Int = 14

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int, width: Int): Boolean {
        hidden = !hidden
        parent?.update()
        return true
    }
}