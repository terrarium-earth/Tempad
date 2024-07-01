package earth.terrarium.tempad.client.widgets.location_panel

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack
import earth.terrarium.olympus.client.components.lists.ListEntry
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.common.utils.sprites
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.util.UUID

class LocationEntry(val id: UUID, val data: LocationData, builder: LocationEntryBuilder.() -> Unit = {}): ListEntry<Any> {
    companion object {
        val FAVORITE = "favorite".sprites("misc")
    }

    init {
        LocationEntryBuilder().apply(builder).also {
            onClick = it.onClick
            isSelected = it.isSelected
            isFavorite = it.isFavorite
        }
    }

    val text = Component.literal(data.name)
    var onClick: () -> Boolean
    var isSelected: () -> Boolean
    var isFavorite: () -> Boolean

    override fun render(
        graphics: GuiGraphics,
        scissor: ScissorBoxStack,
        x: Int,
        y: Int,
        width: Int,
        mouseX: Int,
        mouseY: Int,
        listHovered: Boolean,
        partialTick: Float
    ) {
        val selected = isSelected()
        val favorite = isFavorite()
        val hovered = listHovered && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 12

        if (selected) {
            graphics.fill(x, y, x + width, y + 12, Tempad.ORANGE.value)
        }

        if (favorite) {
            graphics.blitSprite(FAVORITE.get(!selected, hovered), x + 2, y + 1, 10, 10)
        }

        val color = if (selected) 0x000000 else if (hovered) Tempad.HIGHLIGHTED_ORANGE.value else Tempad.ORANGE.value

        graphics.drawString(Minecraft.getInstance().font, text, x + 2 + if(favorite) 14 else 0, y + 2, color, false)
    }

    override fun getHeight(p0: Int): Int = 12

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int, width: Int): Boolean = onClick()
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
