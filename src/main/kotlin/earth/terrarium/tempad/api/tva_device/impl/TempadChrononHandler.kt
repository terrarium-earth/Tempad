package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.common.registries.chrononContentTempad
import earth.terrarium.tempad.common.registries.chrononContentTimeTwister
import earth.terrarium.tempad.common.registries.twisterEquipped
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack

class TempadChrononHandler(val stack: ItemStack, val tempadLimit: Int, timeTwisterLimit: Int): ChrononHandler {
    override var power: Int
        get() = stack.chrononContentTempad + stack.chrononContentTimeTwister
        set(value) = run {
            // put in tempad first, then time twister
            val diff = value - power
            if (diff == 0) return
            // get the difference, put it in tempad first
            val tempadDiff = diff.coerceAtMost(tempadLimit - stack.chrononContentTempad)
            stack.chrononContentTempad += tempadDiff
            stack.chrononContentTimeTwister += diff - tempadDiff
        }

    override val maxPower: Int = tempadLimit + if(stack.twisterEquipped) timeTwisterLimit else 0

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