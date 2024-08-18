package earth.terrarium.tempad.client.widgets.buttons

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.Priority
import earth.terrarium.tempad.common.utils.btnSprites
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import java.util.*
import kotlin.enums.EnumEntries

class EnumButton<T: Enum<T>>(val enumEntries: EnumEntries<T>, val onPress: (value: T) -> Unit): Button(0, 0, 16, 16, CommonComponents.EMPTY, {}, DEFAULT_NARRATION) {
    var sprites: WidgetSprites = enumEntries.first().name.lowercase(Locale.ROOT).btnSprites()
    var selected: T = enumEntries.first()
        set(value) {
            field = value
            this.tooltip = Tooltip.create(Component.translatable("button.${Tempad.MOD_ID}.${value.name.lowercase(Locale.ROOT)}"))
            this.sprites = value.name.lowercase(Locale.ROOT).btnSprites()
        }

    init {
        this.tooltip = Tooltip.create(Component.translatable("button.${Tempad.MOD_ID}.${selected.name.lowercase(Locale.ROOT)}"))
    }

    public override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val resourceLocation = sprites[this.isActive, isHovered()]
        graphics.blitSprite(resourceLocation, this.x, this.y, this.width, this.height)
    }

    override fun onPress() {
        val nextIndex = (selected.ordinal + 1) % enumEntries.size
        selected = enumEntries[nextIndex]
        onPress(selected)
    }
}
