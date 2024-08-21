package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.client.tooltip.ChrononData
import earth.terrarium.tempad.client.tooltip.ChrononTooltip
import earth.terrarium.tempad.client.tooltip.tooltip
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.*

class SacredChronometerItem: Item(Properties().stacksTo(1)) {
    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.of(ChrononData.infinite)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity.tickCount % 24 != 0) return
        if (entity !is Player) return

        ContextRegistry.locate(entity) {
            it.item is ChrononItem && it.chrononContainer.hasRoom && it !== stack
        }?.let {
            it.stack.chrononContainer += it.stack.chrononContainer.capacity
        }
    }
}

class InfiniteChronons(stack: ItemStack): ChrononContainer(stack, Int.MAX_VALUE) {
    override var content: Int
        get() = Int.MAX_VALUE
        set(value) {}
}