package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.client.widgets.KListWidgetItem
import earth.terrarium.tempad.common.utils.btnSprites
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import java.util.UUID

class LocationEntry(val id: UUID, val data: NamedGlobalVec3, builder: LocationEntryBuilder.() -> Unit = {}):
    KListWidgetItem, Comparable<LocationEntry> {
    companion object {
        val FAVORITE = "unpin".btnSprites()
    }

    init {
        LocationEntryBuilder().apply(builder).also {
            onClick = it.onClick
            isSelected = it.isSelected
            isFavorite = it.isFavorite
        }
    }

    var onClick: () -> Boolean
    var isSelected: () -> Boolean
    var isFavorite: () -> Boolean

    override var _x: Int = 0
    override var _y: Int = 0
    override var _focused: Boolean = false
    override var _width: Int = 0
    override var _height: Int = 12

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, pPartialTick: Float) {
        val selected = isSelected()
        val favorite = isFavorite()
        val hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height

        if (selected) {
            graphics.fill(x + 2, y, x + width - 1, y + height, Tempad.ORANGE.value)
        }

        if (favorite) {
            graphics.blitSprite(FAVORITE.get(!selected, hovered), x + 3, y + (height - 10) / 2, 10, 10)
        }

        val color = if (selected) 0x000000 else if (hovered) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value

        val font = Minecraft.getInstance().font
        graphics.drawString(font, data.name, x + 4 + if(favorite) 11 else 0, y + Math.round((height - font.lineHeight) / 2f), color, false)
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = onClick()

    override fun compareTo(other: LocationEntry): Int {
        return if(isFavorite()) Int.MIN_VALUE else if(other.isFavorite()) Int.MAX_VALUE else data.name.string.compareTo(other.data.name.string)
    }
}


class LocationEntryBuilder {
    var onClick: () -> Boolean = { false }
    var isSelected: () -> Boolean = { false }
    var isFavorite: () -> Boolean = { false }

    fun onClick(onClick: () -> Boolean): LocationEntryBuilder {
        this.onClick = onClick
        return this
    }

    fun isSelected(isSelected: () -> Boolean): LocationEntryBuilder {
        this.isSelected = isSelected
        return this
    }

    fun isFavorite(isFavorite: () -> Boolean): LocationEntryBuilder {
        this.isFavorite = isFavorite
        return this
    }
}
