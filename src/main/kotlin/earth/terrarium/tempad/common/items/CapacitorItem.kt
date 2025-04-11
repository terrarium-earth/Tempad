package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.move
import earth.terrarium.tempad.common.utils.contents
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

open class CapacitorItem: ChrononItem() {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity.tickCount % 10 != 0 || cannotDistribute(entity, stack)) return // 1 mb every 0.5 seconds
        distribute(entity as Player, stack)
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action == ClickAction.SECONDARY && slot.hasItem() && slot.contents.chronons != null) {
            safeLet(stack.chronons, slot.contents.chronons) { from, to ->
                move(from, to, 1000)
            }
            return true
        }
        return super.overrideStackedOnOther(stack, slot, action, player)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val to = context.level.getBlockEntity(context.clickedPos)?.chronons ?: return super.useOn(context)
        if (!context.level.isClientSide) {
            context.itemInHand.chronons?.let {
                move(it, to, 1000)
            }
        }
        return InteractionResult.SUCCESS
    }
}