package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.locations.DirectLocation
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.common.menu.WalletMenu
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.registries.walletContents
import earth.terrarium.tempad.common.utils.contents
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

val ItemStack.items: WalletInventory get() = WalletInventory(this)
class WalletItem() : Item(Properties().stacksTo(1)) {

    override fun use(
        level: Level,
        player: Player,
        usedHand: InteractionHand,
    ): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(usedHand)
        player.openMenu(stack.items)
        return InteractionResultHolder.success(stack)
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action != ClickAction.SECONDARY) return false
        val items = stack.items
        if (!items.isFull && slot.contents.item === ModItems.locationCard && slot.contents.portalTarget != null) {
            slot.contents = items.insertItem(slot.contents, false)
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
            access.set(items.insertItem(other, false))
            return true
        }
        return false
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag,
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        val stream = stack.walletContents.stream()
        var empty = true
        tooltipComponents.add(Component.translatable("item.tempad.card_wallet.prefix").withStyle(ChatFormatting.GRAY))
        for (stack in stream) {
            val portalTarget = stack.portalTarget
            if (portalTarget is IndirectLocation) {
                tooltipComponents.add(portalTarget.info)
            } else if (portalTarget is DirectLocation) {
                tooltipComponents.add(MutableComponent.create(portalTarget.location.name.contents).withColor(portalTarget.location.color.value))
            }
            empty = false
        }
        if (empty) {
            tooltipComponents.add(Component.translatable("item.tempad.card_wallet.empty").withStyle(ChatFormatting.DARK_GRAY).withStyle(
                ChatFormatting.ITALIC))
        }
    }
}