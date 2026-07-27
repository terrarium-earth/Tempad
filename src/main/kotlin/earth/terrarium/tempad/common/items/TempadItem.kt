package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.app.TempadAppRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.contents
import earth.terrarium.tempad.common.utils.ctx
import earth.terrarium.tempad.common.utils.getSlot
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class TempadItem(props: Properties) : ChrononItem(props) {
    override fun inventoryTick(
        itemStack: ItemStack,
        level: ServerLevel,
        owner: Entity,
        slot: EquipmentSlot?,
    ) {
        super.inventoryTick(itemStack, level, owner, slot)
        if (itemStack.serialNumber == null) {
            itemStack.serialNumber = Component.literal("${itemStack.serialPrefix}-${owner.random.nextInt(1000, 10_000)}-${owner.plainTextName.take(4).uppercase()}")
        }
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)
        if (level.isClientSide) return InteractionResult.SUCCESS

        val slotId = hand.getSlot(player)
        val access = player.ctx(slotId)
        val accessAddress = ItemAccessAddress(ModItemAccess.inventory, slotId)

        if (player.isShiftKeyDown) {
            MacroRegistry[stack.defaultMacro]?.run(player, access)
        } else {
            (TempadAppRegistry[stack.defaultApp, player, accessAddress]?: TempadAppRegistry[ModApps.teleport, player, accessAddress])!!.openMenu(player as ServerPlayer)
        }

        return InteractionResult.SUCCESS
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action == ClickAction.SECONDARY) {
            if (stack.twisterEquipped) {
                if (!slot.hasItem()) {
                    stack.twisterEquipped = false
                    slot.contents = ModItems.timeTwister.stack {
                        stack.twisterData?.let(::applyComponents)
                        chrononContent = stack.chrononContentTimeTwister
                        stack.chrononContentTimeTwister = 0
                    }
                    return true
                } else if (slot.contents.item === ModItems.tempad && !slot.contents.twisterEquipped) {
                    stack.twisterEquipped = false
                    slot.contents.twisterEquipped = true
                    slot.contents.chrononContentTimeTwister = stack.chrononContentTimeTwister
                    slot.contents.twisterData = stack.twisterData
                    stack.chrononContentTimeTwister = 0
                    stack.twisterData = null
                    return true
                }
            } else if (slot.contents.item === ModItems.timeTwister) {
                stack.twisterEquipped = true
                stack.chrononContentTimeTwister += slot.contents.chrononContent
                stack.twisterData = slot.contents.componentsPatch
                slot.contents = ItemStack.EMPTY
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
            stack.chrononContentTimeTwister += other.chrononContent
            stack.twisterData = other.componentsPatch
            access.set(ItemStack.EMPTY)
            return true
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access)
    }
}