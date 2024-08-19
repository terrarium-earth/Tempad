package earth.terrarium.tempad.client.tooltip

import earth.terrarium.tempad.api.locations.PlayerPos
import net.minecraft.network.chat.Component

class PlayerPosTooltip(pos: PlayerPos): MultilineTooltip {
    override val text: List<Component> = listOf(
        Component.translatable("tempad.tooltip.player_pos", pos.playerProfile.name)
    )
}