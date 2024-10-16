package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.common.registries.chrononContentTempad
import earth.terrarium.tempad.common.registries.chrononContentTimeTwister
import net.minecraft.world.item.ItemStack

class TempadChrononHandler(val stack: ItemStack, val tempadLimit: Int, timeTwisterLimit: Int): ChrononHandler {
    override var power: Int
        get() = stack.chrononContentTempad + stack.chrononContentTimeTwister
        set(value) = run {
            // put in tempad first, then time twister
            val diff = value - power
            if (diff == 0) return
            if (diff > 0) {
                // get the difference, put it in tempad first
                val tempadDiff = diff.coerceAtMost(tempadLimit - stack.chrononContentTempad)
                stack.chrononContentTempad += tempadDiff
                stack.chrononContentTimeTwister += diff - tempadDiff
            } else {
                // drain from tempad first
                val tempadDiff = diff.coerceAtMost(stack.chrononContentTempad)
                stack.chrononContentTempad -= tempadDiff
                stack.chrononContentTimeTwister -= diff - tempadDiff
            }
        }

    override val maxPower: Int = tempadLimit + timeTwisterLimit

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