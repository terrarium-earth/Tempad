package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.client.tooltip.tooltip
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.transfer.access.ItemAccess
import java.util.*
import kotlin.to

class TemputerIron(props: Properties) : BlockItem(ModBlocks.temputerIron, props) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        player.ctx(usedHand.getSlot(player)).openTimedoor(player)
        return InteractionResult.SUCCESS
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.player?.isShiftKeyDown == true) return super.useOn(context)
        val player = context.player ?: return InteractionResult.PASS
        context.syncableCtx?.openTimedoor(player)
        return InteractionResult.PASS
    }

    fun ItemAccess.openTimedoor(player: Player) {
        val portalTarget = resource.portalTarget
        portalTarget?.get(null, chronons)?.let { pos ->
            if (!player.level().isClientSide) {
                val (provider, id) = (portalTarget as? IndirectLocation).let {
                    it?.provider to it?.id
                }
                val cost = portalTarget.calculateCost(player.level() as ServerLevel, player.blockPosition(), null, chronons)
                TimedoorEntity.openTimedoor(player, this, provider, id, pos, cost) {
                    it.instability = 15
                }
            }
        } ?: {
            if (!player.level().isClientSide) player.sendSystemMessage(TimedoorEntity.posFail)
        }
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action != ClickAction.SECONDARY) return false
        if (stack.portalTarget != null) {
            if (slot.hasItem()) return false
            slot.contents = ModItems.locationCard.stack {
                portalTarget = stack.portalTarget
            }
            stack.portalTarget = null
            return true
        } else if (slot.contents.item === ModItems.locationCard && slot.contents.portalTarget != null) {
            stack.portalTarget = slot.contents.portalTarget
            slot.contents -= 1
            return true
        }
        return false
    }

    override fun updateCustomBlockEntityTag(
        pos: BlockPos,
        level: Level,
        player: Player?,
        stack: ItemStack,
        state: BlockState,
    ): Boolean {
        level.getBlockEntity(pos)?.let { blockEntity ->
            if (blockEntity !is RudimentaryTempadBE) return@let
            player?.let { blockEntity.owner = it.gameProfile }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state)
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess,
    ): Boolean {
        if (stack.portalTarget == null) {
            if (other.item === ModItems.locationCard && other.portalTarget != null) {
                stack.portalTarget = other.portalTarget
                access.set(other - 1)
                return true
            }
        }
        return false
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.ofNullable(stack.access.chronons?.tooltip)
    }

    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        return false
    }
}