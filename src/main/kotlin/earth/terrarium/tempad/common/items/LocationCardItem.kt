package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.context.modify
import earth.terrarium.tempad.api.locations.DirectLocation
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.portalTarget
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.*

class LocationCardItem: Item(Properties().stacksTo(16)) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val cardStack = player.getItemInHand(usedHand)
        if (!level.isClientSide) {
            cardStack.portalTarget?.let { pos ->
                val ctx = ContextRegistry.locate(player) { it.item == ModItems.cardWallet && it.items.insertItem(cardStack, true).count < cardStack.count }
                ctx?.modify {
                    val leftOver = it.items.insertItem(cardStack, false)
                    player.setItemInHand(usedHand, leftOver)
                    player.displayClientMessage(Component.translatable("item.tempad.card_wallet.inserted").withColor(Tempad.ORANGE.value), true)
                } ?: player.displayClientMessage(Component.translatable("item.tempad.card_wallet.insert_fail").withColor(Tempad.HIGHLIGHTED_ORANGE.value), true)
            }
        }
        return InteractionResultHolder.success(cardStack)
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.ofNullable(stack.portalTarget as? TooltipComponent)
    }
}