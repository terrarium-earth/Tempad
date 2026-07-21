package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.common.registries.enabled
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class LocationBroadcasterItem(props: Properties): Item(props) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (!level.isClientSide) {
            val stack = player.getItemInHand(hand)
            stack.enabled = !stack.enabled
        }

        return InteractionResult.SUCCESS
    }
}