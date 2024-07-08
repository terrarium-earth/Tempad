package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.network.s2c.OpenTimeTwisterScreen
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.defaultMacro
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.utils.getSlot
import earth.terrarium.tempad.common.utils.sendToClient
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class TimeTwisterItem: Item(Properties()) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        if (!level.isClientSide) {
            OpenTimeTwisterScreen(player.travelHistory).sendToClient(player)
            return InteractionResultHolder.success(stack)
        }
        return InteractionResultHolder.success(stack)
    }
}