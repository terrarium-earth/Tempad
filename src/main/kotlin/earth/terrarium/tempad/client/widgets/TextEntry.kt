package earth.terrarium.tempad.client.widgets

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack
import com.teamresourceful.resourcefullib.common.color.ConstantColors
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.ProviderSettings
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.util.UUID

class TextEntry : KListEntry {
    val data: LocationData?
    val id: UUID?
    val settings: ProviderSettings?
    private var favoritePredicate: (ProviderSettings?, UUID?) -> Boolean = { _, _ -> false }
    private val component: Component
    private val isSelectable: Boolean
    private val color: Int
    private var shadow = true
    val isFavorite: Boolean
        get() = favoritePredicate(settings, id)

    override var isFocusedOn = false

    constructor(
        settings: ProviderSettings,
        id: UUID,
        data: LocationData,
        isFavorite: (ProviderSettings?, UUID?) -> Boolean
    ) {
        this.settings = settings
        this.id = id
        this.data = data
        this.component = Component.translatable(data.name)
        this.favoritePredicate = isFavorite
        isSelectable = true
        this.color = Tempad.ORANGE.value
    }

    constructor(component: Component) {
        this.settings = null
        this.id = null
        this.data = null
        this.component = component
        isSelectable = false
        this.color = Tempad.ORANGE.value
    }

    constructor(component: Component, color: Int, shadow: Boolean) {
        this.settings = null
        this.id = null
        this.data = null
        this.component = component
        isSelectable = false
        this.color = color
        this.shadow = shadow
    }

    override fun render(
        graphics: GuiGraphics,
        stack: ScissorBoxStack,
        id: Int,
        left: Int,
        top: Int,
        width: Int,
        height: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        partialTick: Float,
        selected: Boolean
    ) {
        val isMouseOver = mouseX > left && mouseX < left + width && mouseY > top && mouseY < top + height

        if (isSelectable && selected) {
            graphics.fill(left, top, left + width, top + height, color)
        }

        val renderedComponent =
            if (isFavorite) Component.literal("❤ ").append(component) else if (!isSelectable) MutableComponent.create(
                component.contents
            ).withStyle(
                ChatFormatting.UNDERLINE
            ) else component

        val color =
            if ((selected && isSelectable)) ConstantColors.black.value else if (isMouseOver || !isSelectable) this.color else ConstantColors.darkorange.value

        graphics.drawString(
            Minecraft.getInstance().font,
            renderedComponent,
            left + 2,
            top + 2,
            color,
            shadow && !(selected && isSelectable)
        )
    }
}
