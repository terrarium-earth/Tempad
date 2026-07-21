package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.client.tooltip.tooltip
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.utils.access
import earth.terrarium.tempad.common.utils.subTransfer
import earth.terrarium.tempad.common.utils.commit
import earth.terrarium.tempad.common.utils.contains
import earth.terrarium.tempad.common.utils.hasRoom
import earth.terrarium.tempad.common.utils.move
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.transaction.Transaction
import java.util.*

abstract class ChrononItem(props: Properties): Item(props) {
    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.ofNullable(stack.access.chronons?.tooltip)
    }

    fun cannotDistribute(entity: Entity, stack: ItemStack): Boolean {
        return entity !is Player || ItemAccessRegistry.locate(entity) { isChargable(stack, it) } == null
    }

    context(txn: Transaction)
    fun distribute(player: Player, stack: ItemStack) {
        ItemAccessRegistry.locate(player) { isChargable(stack, it) }?.let {
            safeLet(stack.access.chronons, it.getAccess(player).chronons) { from, to ->
                subTransfer {
                    from.move(to, Int.MAX_VALUE)
                    commit()
                }
            }
        }
    }

    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        return false
    }

    open fun isChargable(source: ItemStack, target: ItemStack) = target.access.chronons?.hasRoom == true && target !== source && target !in ModTags.chargeBlacklist
}