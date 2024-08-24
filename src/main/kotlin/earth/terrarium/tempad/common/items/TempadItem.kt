package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.client.tooltip.tooltip
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.contents
import earth.terrarium.tempad.common.utils.ctx
import earth.terrarium.tempad.common.utils.getSlot
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import java.util.*

class TempadItem : ChrononItem() {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        if (level.isClientSide) return InteractionResultHolder.success(stack)

        val slotId = hand.getSlot(player)
        val ctx = player.ctx(slotId)

        if (player.isShiftKeyDown) {
            MacroRegistry[stack.defaultMacro]?.run(player, ctx)
        } else {
            (AppRegistry[stack.defaultApp, ctx]?: AppRegistry[ModApps.teleport, ctx])!!.openMenu(player as ServerPlayer)
        }

        return InteractionResultHolder.success(stack)
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action == ClickAction.SECONDARY) {
            if (stack.twisterEquipped) {
                if (!slot.hasItem()) {
                    stack.twisterEquipped = false
                    slot.contents = stack.transmuteCopy(ModItems.timeTwister).also {
                        val toMove = it.chrononContent.coerceAtMost(it.chrononContainer.capacity)
                        it.chrononContent = toMove
                        stack.chrononContent -= toMove
                    }
                    return true
                } else if (slot.contents.item === ModItems.tempad) {
                    val toMove = stack.chrononContent.coerceAtMost(4000)
                    stack.chrononContent -= toMove
                    stack.twisterEquipped = false
                    slot.contents.twisterEquipped = true
                    slot.contents.chrononContent += toMove
                    return true
                }
            } else if (slot.contents.item === ModItems.timeTwister) {
                val chronons = slot.contents.chrononContent
                slot.contents = ItemStack.EMPTY
                stack.twisterEquipped = true
                stack.chrononContent += chronons
                return true
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player)
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess,
    ): Boolean {
        if (action == ClickAction.SECONDARY && other.item === ModItems.timeTwister && !stack.twisterEquipped) {
            stack.twisterEquipped = true
            stack.chrononContent += other.chrononContent
            access.set(ItemStack.EMPTY)
            return true
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access)
    }

    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean = false

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}

class TempadContainer(stack: ItemStack) : ItemChrononContainer(stack, 0) {
    override val capacity: Int get() = if (stack.twisterEquipped) 12000 else 8000
}