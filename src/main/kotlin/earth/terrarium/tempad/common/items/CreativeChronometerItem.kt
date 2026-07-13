package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.client.tooltip.ChrononData
import earth.terrarium.tempad.common.utils.access
import earth.terrarium.tempad.common.utils.hasRoom
import earth.terrarium.tempad.common.utils.insert
import earth.terrarium.tempad.common.utils.move
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.transfer.access.ItemAccess
import java.util.*

class CreativeChronometerItem : Item(Properties().stacksTo(1)) {
    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.of(ChrononData.infinite)
    }

    override fun inventoryTick(
        itemStack: ItemStack,
        level: ServerLevel,
        owner: Entity,
        slot: EquipmentSlot?,
    ) {
        super.inventoryTick(itemStack, level, owner, slot)
        if (level.isClientSide || owner.tickCount % 10 != 0) return
        if (owner !is Player) return

        transfer {
            ItemAccessRegistry.locate(owner) {
                it.access.chronons?.hasRoom == true && it !== itemStack
            }?.getAccess(owner)?.chronons?.insert(Int.MAX_VALUE)
        }
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val to = context.level.getBlockEntity(context.clickedPos)?.chronons ?: return super.useOn(context)
        val player = context.player
        if (!context.level.isClientSide && player != null) {
            transfer {
                ItemAccess.forPlayerInteraction(player, context.hand).chronons?.move(to, Int.MAX_VALUE)
            }
        }
        return InteractionResult.SUCCESS
    }
}