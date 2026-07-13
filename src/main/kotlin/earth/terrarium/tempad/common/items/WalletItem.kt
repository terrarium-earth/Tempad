package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.locations.DirectLocation
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.common.menu.WalletMenu
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.registries.walletContents
import earth.terrarium.tempad.common.utils.contents
import earth.terrarium.tempad.common.utils.minus
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.transaction.Transaction
import java.util.function.Consumer

class WalletItem() : Item(Properties().stacksTo(1)) {

    override fun use(
        level: Level,
        player: Player,
        hand: InteractionHand,
    ): InteractionResult {
        val stack = player.getItemInHand(hand)
        player.openMenu(WalletInventory(ItemAccess.forPlayerInteraction(player, hand)))
        return InteractionResult.SUCCESS
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action != ClickAction.SECONDARY) return false
        val items = stack.items
        if (!items.isFull && slot.contents.item === ModItems.locationCard && slot.contents.portalTarget != null) {
            Transaction.openRoot().use { transaction ->
                val inserted = items.insert(ItemResource.of(slot.contents), slot.contents.count, transaction)
                slot.contents -= inserted
                transaction.commit()
            }
            return true
        }
        return false
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess,
    ): Boolean {
        val items = stack.items
        if (!items.isFull && other.item === ModItems.locationCard && other.portalTarget != null && player.containerMenu !is WalletMenu) {
            /*access.set(items.insertItem(other, false))
            return true*/
            Transaction.openRoot().use { transaction ->
                val inserted = items.insert(ItemResource.of(other), other.count, transaction)
                access.set(other.copy().also { it.shrink(inserted) })
                transaction.commit()
            }
        }
        return false
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag)
        val stream = stack.walletContents.allItemsCopyStream()
        var empty = true
        builder.accept(Component.translatable("item.tempad.card_wallet.prefix").withStyle(ChatFormatting.GRAY))
        for (stack in stream) {
            val portalTarget = stack.portalTarget
            if (portalTarget is IndirectLocation) {
                builder.accept(portalTarget.info)
            } else if (portalTarget is DirectLocation) {
                builder.accept(MutableComponent.create(portalTarget.location.name.contents).withColor(portalTarget.location.color.value))
            }
            empty = false
        }
        if (empty) {
            builder.accept(Component.translatable("item.tempad.card_wallet.empty").withStyle(ChatFormatting.DARK_GRAY).withStyle(
                ChatFormatting.ITALIC))
        }
    }
}