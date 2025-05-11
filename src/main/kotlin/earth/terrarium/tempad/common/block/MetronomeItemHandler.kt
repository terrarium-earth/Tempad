package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.tva_device.chronons
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler

class MetronomeItemHandler: ItemStackHandler(8) {
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return slot < 4 && stack.chronons?.canExtract == true || slot >= 4 && stack.chronons?.canInsert == true
    }
}