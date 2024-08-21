package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.common.registries.enabled
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class StatusEmitterItem: Item(Properties().stacksTo(1)) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide) {
            val stack = player.getItemInHand(hand)
            stack.enabled = !stack.enabled
        }

        return InteractionResultHolder.success(player.getItemInHand(hand))
    }
}
