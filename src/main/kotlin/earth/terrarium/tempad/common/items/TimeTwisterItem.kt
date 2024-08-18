package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.network.s2c.OpenTimeTwister
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.utils.ctx
import earth.terrarium.tempad.common.utils.getSlot
import earth.terrarium.tempad.common.utils.sendToClient
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class TimeTwisterItem : Item(Properties().stacksTo(1)), ChrononAcceptor {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide) {
            val ctx = player.ctx(usedHand.getSlot(player))
            if(ctx.stack.chrononContent < 1000) {
                player.displayClientMessage(Component.translatable("message.tempad.not_enough_chronons").withColor(Tempad.ORANGE.value), true)
                return InteractionResultHolder.fail(player.getItemInHand(usedHand))
            }
            OpenTimeTwister(player.travelHistory.relevantHistory, ctx.holder).sendToClient(player)
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }

    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean = false
}