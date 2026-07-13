package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.utils.access
import earth.terrarium.tempad.common.utils.commit
import earth.terrarium.tempad.common.utils.contains
import earth.terrarium.tempad.common.utils.move
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil
import net.neoforged.neoforge.transfer.transaction.Transaction

open class CapacitorItem: ChrononItem() {
    override fun inventoryTick(
        stack: ItemStack,
        level: ServerLevel,
        owner: Entity,
        slot: EquipmentSlot?,
    ) {
        super.inventoryTick(stack, level, owner, slot)
        if (level.isClientSide || owner.tickCount % 10 != 0 || cannotDistribute(owner, stack)) return // 1 mb every 0.5 seconds
        transfer {
            distribute(owner as Player, stack)
        }
    }

    override fun isChargable(source: ItemStack, target: ItemStack): Boolean {
        return super.isChargable(source, target) && target !in ModTags.batteries
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action == ClickAction.SECONDARY && slot.hasItem() && slot.access.chronons != null) {
            safeLet(stack.access.chronons, slot.access.chronons) { from, to ->
                Transaction.openRoot().use {
                    EnergyHandlerUtil.move(from, to, 1000, it)
                }
            }
            return true
        }
        return super.overrideStackedOnOther(stack, slot, action, player)
    }

    override fun overrideOtherStackedOnMe(
        self: ItemStack,
        other: ItemStack,
        slot: Slot,
        clickAction: ClickAction,
        player: Player,
        carriedItem: SlotAccess,
    ): Boolean {
        return super.overrideOtherStackedOnMe(self, other, slot, clickAction, player, carriedItem)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val block = context.level.getBlockEntity(context.clickedPos)?.chronons ?: return super.useOn(context)
        if (!context.level.isClientSide) {
            context.itemInHand.access.chronons?.let { handler ->
                transfer {
                    if(handler.amountAsInt == 0) {
                        block.move(handler, handler.capacityAsInt)
                    } else if (block.amountAsInt == 0) {
                        handler.move(block, block.capacityAsInt)
                    }
                    commit()
                }
            }
        }
        return InteractionResult.SUCCESS
    }
}