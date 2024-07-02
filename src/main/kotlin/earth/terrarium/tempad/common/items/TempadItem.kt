package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.common.data.settings
import earth.terrarium.tempad.common.utils.getSlot
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class TempadItem: Item(Properties()) {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        if (level.isClientSide) return InteractionResultHolder.success(stack)

        AppRegistry.get(stack.settings.defaultApp, hand.getSlot(player))?.openMenu(player as ServerPlayer)

        return InteractionResultHolder.success(stack)
    }
}