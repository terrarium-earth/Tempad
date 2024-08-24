package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.common.registries.ModFluids
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.utils.contains
import earth.terrarium.tempad.common.utils.contents
import earth.terrarium.tempad.common.utils.get
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class ChronometerItem : ChrononItem() {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.isClientSide || entity.tickCount % 24 != 0) return // 1 mb every 1.2 seconds, 1 bucket per day
        if (entity !is Player || ContextRegistry.locate(entity) { it.item is ChronometerItem && it !== stack } != null) return

        stack.chrononContainer += 1

        ContextRegistry.locate(entity) {
            it in ModTags.chrononAcceptor && it.chrononContainer.hasRoom && it !== stack
        }?.let {
            move(stack.chrononContainer, it.stack.chrononContainer, 1000)
        }
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action == ClickAction.SECONDARY && slot.hasItem() && slot.contents in ModTags.chrononAcceptor) {
            move(stack.chrononContainer, slot.contents.chrononContainer, 1000)
            return true
        }
        return super.overrideStackedOnOther(stack, slot, action, player)
    }
}