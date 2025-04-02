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

class ChronometerItem : CapacitorItem() {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity.tickCount % CommonConfig.Chronometer.generationRate != 0 || cannotDistribute(entity, stack)) return // 1 mb every 1.2 seconds, 1 bucket per day
        stack.chronons?.insert(CommonConfig.Chronometer.generationAmount, ActionType.Execute)
        if (stack.chronons != null) {
            distribute(entity as Player, stack)
        } else {
            ContextRegistry.locate(entity as Player) {
                it.chronons?.hasRoom == true && it !== stack
            }?.let {
                it.stack.chronons?.insert(CommonConfig.Chronometer.generationAmount, ActionType.Execute)
            }
        }
    }
}