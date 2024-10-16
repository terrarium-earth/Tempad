package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.hasRoom
import earth.terrarium.tempad.api.tva_device.move
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.utils.contains
import earth.terrarium.tempad.common.utils.contents
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class ChronometerItem : ChrononItem() {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity.tickCount % 24 != 0) return // 1 mb every 1.2 seconds, 1 bucket per day
        if (entity !is Player || ContextRegistry.locate(entity) { it.item is ChronometerItem && it !== stack } != null) return

        stack.chronons?.insert(1, ActionType.Execute)

        ContextRegistry.locate(entity) {
            it.chronons?.hasRoom == true && it !== stack
        }?.let {
            safeLet(stack.chronons, it.stack.chronons) { from, to ->
                move(from, to, 1)
            }
        }
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action == ClickAction.SECONDARY && slot.hasItem() && slot.contents in ModTags.chrononAcceptor) {
            safeLet(stack.chronons, slot.contents.chronons) { from, to ->
                move(from, to, 1)
            }
            return true
        }
        return super.overrideStackedOnOther(stack, slot, action, player)
    }
}