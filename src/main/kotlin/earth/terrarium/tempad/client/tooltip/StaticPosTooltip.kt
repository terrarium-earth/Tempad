package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

class StaticPosTooltip(pos: NamedGlobalVec3) : MultilineTooltip {
    override val text: List<Component> = listOf(
        MutableComponent.create(pos.name.contents).withColor(Tempad.ORANGE.value),
        pos.dimensionText.withStyle(ChatFormatting.GRAY),
        Component.literal("X: ${pos.x}").withStyle(ChatFormatting.DARK_GRAY),
        Component.literal("Y: ${pos.y}").withStyle(ChatFormatting.DARK_GRAY),
        Component.literal("Z: ${pos.z}").withStyle(ChatFormatting.DARK_GRAY)
    )
}