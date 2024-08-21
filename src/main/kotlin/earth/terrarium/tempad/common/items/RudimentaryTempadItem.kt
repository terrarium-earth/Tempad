package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.client.tooltip.tooltip
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.staticLocation
import earth.terrarium.tempad.common.utils.*
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import java.util.*

class RudimentaryTempadItem: BlockItem(ModBlocks.rudimentaryTempad, Properties().stacksTo(1)) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (level.isClientSide) return InteractionResultHolder.success(player.getItemInHand(usedHand))
        val ctx = player.ctx(usedHand.getSlot(player))
        ctx.stack.staticLocation?.let {
            TimedoorEntity.openTimedoor(player, ctx, it)
        } ?: {
            player.displayClientMessage("item.tempad.rudimentary_tempad.no_location".translate, true)
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }



    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action != ClickAction.SECONDARY) return false
        if (stack.staticLocation != null) {
            if (slot.hasItem()) return false
            slot.contents = ModItems.locationCard.stack {
                staticLocation = stack.staticLocation
            }
            stack.staticLocation = null
            return true
        } else if (slot.contents.item === ModItems.locationCard && slot.contents.staticLocation != null) {
            stack.staticLocation = slot.contents.staticLocation
            slot.contents -= 1
            return true
        }
        return false
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess,
    ): Boolean {
        if (stack.staticLocation == null) {
            if (other.item === ModItems.locationCard && other.staticLocation != null) {
                stack.staticLocation = other.staticLocation
                access.set(other - 1)
                return true
            }
        }
        return false
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.of(stack.chrononContainer.tooltip)
    }
}