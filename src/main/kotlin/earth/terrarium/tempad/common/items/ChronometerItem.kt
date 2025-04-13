package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.hasRoom
import earth.terrarium.tempad.api.tva_device.move
import earth.terrarium.tempad.common.config.CommonConfig
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

class ChronometerItem(val rate: () -> Int, val amount: () -> Int) : CapacitorItem() {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity !is Player || entity.tickCount % rate() != 0 || isClashing(entity, stack)) return // 1 mb every 1.2 seconds, 1 bucket per day
        stack.chronons?.insert(amount(), ActionType.Execute)
        if (stack.chronons != null) {
            distribute(entity, stack)
        } else {
            ContextRegistry.locate(entity) { isChargable(stack, it) }?.let {
                it.stack.chronons?.insert(amount(), ActionType.Execute)
            }
        }
    }

    override fun isChargable(source: ItemStack, target: ItemStack): Boolean {
        return target.chronons?.hasRoom == true && target !== source && target !in ModTags.chargeBlacklist
    }

    fun isClashing(player: Player, stack: ItemStack): Boolean {
        return ContextRegistry.locate(player) { it in ModTags.chrononGens && it !== stack } != null
    }
}