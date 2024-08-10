package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.client.screen.TimeTwisterScreen
import earth.terrarium.tempad.common.network.s2c.OpenTimeTwister
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.utils.sendToClient
import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class TimeTwisterItem : Item(Properties().stacksTo(1)), ChrononAcceptor {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide) {
            OpenTimeTwister(player.travelHistory.relevantHistory).sendToClient(player)
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }
}