package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.common.registries.chrononContent
import net.minecraft.world.item.ItemStack

class ItemChrononHandler(val stack: ItemStack, override val maxPower: Int): ChrononHandler {
    override var power: Int by stack::chrononContent

    override fun extract(amount: Int, action: ActionType): Int {
        val extracted = power.coerceAtMost(amount)
        power -= extracted
        return extracted
    }

    override fun insert(amount: Int, action: ActionType): Int {
        val inserted = maxPower - power
        power += inserted
        return inserted
    }
}