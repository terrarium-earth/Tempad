package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.hasRoom
import earth.terrarium.tempad.api.tva_device.move
import earth.terrarium.tempad.client.tooltip.ChrononData
import earth.terrarium.tempad.client.tooltip.tooltip
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*

abstract class ChrononItem: Item(Properties().stacksTo(1)) {
    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.ofNullable(stack.chronons?.tooltip)
    }

    fun cannotDistribute(entity: Entity, stack: ItemStack): Boolean {
        return entity !is Player || ContextRegistry.locate(entity) { it.chronons?.hasRoom == true && it !== stack } == null
    }

    fun distribute(player: Player, stack: ItemStack) {
        ContextRegistry.locate(player) {
            it.chronons?.hasRoom == true && it !== stack
        }?.let {
            safeLet(stack.chronons, it.stack.chronons) { from, to ->
                move(from, to, Int.MAX_VALUE)
            }
        }
    }

    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        return false
    }
}