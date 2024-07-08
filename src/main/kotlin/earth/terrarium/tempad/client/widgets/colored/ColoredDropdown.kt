package earth.terrarium.tempad.client.widgets.colored

import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.olympus.client.components.dropdown.Dropdown
import earth.terrarium.olympus.client.components.dropdown.DropdownEntry
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.bedrockButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class ColoredDropdown<T>(
    width: Int,
    val entries: List<T>,
    val optionDisplay: (T) -> Component,
    default: T,
    onSelect: (T) -> Unit
) :
    Dropdown<T>(null, width, 24, entries.associateWith { optionDisplay(it) }, default, onSelect) {

    override fun renderButton(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.bedrockButton(x, y, width, height, isDropdownOpen, isActive, isHovered || isDropdownOpen)
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = Minecraft.getInstance().font
        this.renderButton(graphics, mouseX, mouseY, partialTick)
        val textOffset = (this.height - 8) / 2
        graphics.drawString(
            font,
            this.getText(this.selected),
            this.x + textOffset,
            this.y + textOffset - 1 + if (isDropdownOpen) 2 else 0,
            this.fontColor, false
        )
        val chevronOffset = (this.height - 16) / 2
        graphics.blitSprite(
            this.chevronTexture,
            x + this.width - chevronOffset - 16, this.y + chevronOffset, 16, 16
        )
    }

    override fun getFontColor(): Int = 0xFF000000.toInt()

    override fun getEntryHeight(): Int = 20

    override fun renderEntriesBackground(
        graphics: GuiGraphics,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        graphics.fill(x, y, x + width, y + height, Tempad.DARK_ORANGE.value)
        graphics.renderOutline(x, y, width, height, 0x66000000)
        graphics.renderOutline(x + 1, y + 1, width - 2, height - 2, Tempad.ORANGE.value)
    }

    override fun initEntries(list: ListWidget, width: Int, action: Runnable) {
        for (value in entries) {
            list.add(ColoredEntry(width, entryHeight, this, value) {
                select(value)
                action.run()
            })
        }
    }

    override fun getText(value: T): Component = optionDisplay(value)

    inner class ColoredEntry<T>(width: Int, height: Int, dropdown: Dropdown<T>?, value: T, action: Runnable) :
        DropdownEntry<T>(width, height, dropdown, value, action) {
        override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
            val color: Int = if (!isHovered) Tempad.ORANGE.value else 0xFF000000.toInt()

            if (isHovered) {
                graphics.fill(x, y, x + width, y + height, Tempad.ORANGE.value)
            }

            val text = dropdown.getText(this.value)
            val textOffset = (this.height - 8) / 2
            graphics.drawString(
                Minecraft.getInstance().font, text,
                x + textOffset,
                y + textOffset,
                color,
                !isHovered
            )
            graphics.hLine(x, x + width, y + height, Tempad.ORANGE.value)
        }
    }
}
