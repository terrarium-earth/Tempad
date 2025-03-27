package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.api.locations.IndirectLocation
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

class IndirectPosTooltip(pos: IndirectLocation): MultilineTooltip {
    override val text: List<Component> = listOf(
        Component.translatable("item.tempad.location_card.created_by", Component.literal(pos.accessor.name).withStyle(
            ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY),
        pos.info,
        Component.translatable("item.tempad.location_card.id", pos.id.toString()).withStyle(ChatFormatting.DARK_GRAY),
    )
}