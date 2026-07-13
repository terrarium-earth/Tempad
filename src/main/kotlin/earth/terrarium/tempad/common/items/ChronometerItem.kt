package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.utils.access
import earth.terrarium.tempad.common.utils.commit
import earth.terrarium.tempad.common.utils.contains
import earth.terrarium.tempad.common.utils.hasRoom
import earth.terrarium.tempad.common.utils.insert
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class ChronometerItem(val rate: () -> Int, val amount: () -> Int) : CapacitorItem() {
    override fun inventoryTick(
        stack: ItemStack,
        level: ServerLevel,
        entity: Entity,
        slot: EquipmentSlot?,
    ) {
        super.inventoryTick(stack, level, entity, slot)
        if (level.isClientSide || entity !is Player || entity.tickCount % rate() != 0 || isClashing(entity, stack)) return
        transfer {
            stack.access.chronons?.insert(amount())
            if (stack.access.chronons != null) {
                distribute(entity, stack)
            } else {
                ItemAccessRegistry.locate(entity) { isChargable(stack, it) }?.let {
                    it.getAccess(entity).chronons?.insert(amount())
                }
            }
            commit()
        }
    }

    override fun isChargable(source: ItemStack, target: ItemStack): Boolean {
        return target.access.chronons?.hasRoom == true && target !== source && target !in ModTags.chargeBlacklist
    }

    fun isClashing(player: Player, stack: ItemStack): Boolean {
        return ItemAccessRegistry.locate(player) { it in ModTags.chrononGens && it !== stack } != null
    }
}