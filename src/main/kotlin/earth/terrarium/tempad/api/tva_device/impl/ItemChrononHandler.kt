package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.common.registries.chrononContent
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack

class ItemChrononHandler(val stack: ItemStack, override val maxPower: Int): ChrononHandler {
    companion object {
        fun create(stack: ItemStack, maxPower: Int): ItemChrononHandler? {
            if (maxPower <= 0) return null
            return ItemChrononHandler(stack, maxPower)
        }
    }

    override var canInsert: Boolean = true
    override var canExtract: Boolean = true

    override var power: Int by stack::chrononContent

    override fun extract(amount: Int, action: ActionType): Int {
        val extracted = Mth.clamp(power, 0, amount)
        if(action == ActionType.Execute) power -= extracted
        return extracted
    }

    override fun insert(amount: Int, action: ActionType): Int {
        val inserted = Mth.clamp(amount, 0, maxPower - power)
        if(action == ActionType.Execute) power += inserted
        return inserted
    }
}