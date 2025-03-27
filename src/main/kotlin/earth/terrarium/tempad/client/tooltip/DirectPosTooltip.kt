package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.DirectLocation
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class DirectPosTooltip(pos: DirectLocation) : MultilineTooltip {
    override val text: List<Component> = listOf(
        MutableComponent.create(pos.location.name.contents).withColor(Tempad.ORANGE.value),
        pos.location.dimensionText.withStyle(ChatFormatting.GRAY),
        Component.literal("X: ${pos.location.x}").withStyle(ChatFormatting.DARK_GRAY),
        Component.literal("Y: ${pos.location.y}").withStyle(ChatFormatting.DARK_GRAY),
        Component.literal("Z: ${pos.location.z}").withStyle(ChatFormatting.DARK_GRAY)
    )
}