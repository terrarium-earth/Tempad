package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.utils.commit
import earth.terrarium.tempad.common.utils.insert
import earth.terrarium.tempad.common.utils.items
import earth.terrarium.tempad.common.utils.subTransfer
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.item.ItemResource
import java.util.*

class LocationCardItem(props: Properties): Item(props) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResult {
        val cardStack = player.getItemInHand(usedHand)
        if (!level.isClientSide) {
            cardStack.portalTarget?.let { pos ->
                transfer {
                    var ctx: ItemAccess? = null
                    val card = ItemResource.of(cardStack)
                    subTransfer {
                        ctx = ItemAccessRegistry.locate(player) {
                            it.item == ModItems.cardWallet && it.items.insert(card, cardStack.count) > 0
                        }?.getAccess(player)
                    }

                    ctx?.items?.let { access ->
                        val inserted = access.insert(card, cardStack.count)
                        if (inserted >= cardStack.count) {
                            commit()
                            player.setItemInHand(usedHand, ItemStack.EMPTY)
                        } else if (inserted > 0) {
                            commit()
                            player.setItemInHand(usedHand, cardStack.copyWithCount(cardStack.count - inserted))
                            player.sendOverlayMessage(Component.translatable("item.tempad.card_wallet.inserted").withColor(Tempad.ORANGE.value))
                        } else {
                            player.sendOverlayMessage(Component.translatable("item.tempad.card_wallet.insert_fail").withColor(Tempad.HIGHLIGHTED_ORANGE.value))
                        }
                    }
                }
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.ofNullable(stack.portalTarget as? TooltipComponent)
    }
}