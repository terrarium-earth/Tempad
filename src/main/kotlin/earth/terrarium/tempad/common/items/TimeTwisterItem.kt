package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.network.s2c.OpenTimeTwister
import earth.terrarium.tempad.common.registries.ModItemAccess
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.utils.ctx
import earth.terrarium.tempad.common.utils.getSlot
import earth.terrarium.tempad.common.utils.sendToClient
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class TimeTwisterItem(props: Properties) : ChrononItem(props) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResult {
        if (!level.isClientSide) {
            val ctx = player.ctx(usedHand.getSlot(player))
            if(!player.isCreative && ctx.resource.chrononContent < 1000) {
                player.sendOverlayMessage(Component.translatable("message.tempad.not_enough_chronons").withColor(Tempad.ORANGE.value))
                return InteractionResult.FAIL
            }
            OpenTimeTwister(player.travelHistory.relevantHistory, ItemAccessAddress(ModItemAccess.inventory, usedHand.getSlot(player))).sendToClient(player)
        }
        return InteractionResult.SUCCESS
    }
}