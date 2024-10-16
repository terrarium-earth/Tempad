package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.hasRoom
import earth.terrarium.tempad.client.tooltip.ChrononData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.*

class SacredChronometerItem : Item(Properties().stacksTo(1)) {
    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.of(ChrononData.infinite)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity.tickCount % 10 != 0) return
        if (entity !is Player) return

        ContextRegistry.locate(entity) {
            it.chronons?.hasRoom == true && it !== stack
        }?.let {
            stack.chronons?.insert(1, ActionType.Execute)
        }
    }
}