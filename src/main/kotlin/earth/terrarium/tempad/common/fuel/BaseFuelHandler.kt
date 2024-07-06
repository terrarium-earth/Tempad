package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.api.fuel.FuelHandler
import earth.terrarium.tempad.common.data.fuelCharges
import net.minecraft.world.item.ItemStack

abstract class BaseFuelHandler(val stack: ItemStack): FuelHandler {
    override var charges: Int
        get() = stack.fuelCharges
        set(value) {
            stack.fuelCharges = value
        }

    override operator fun plusAssign(amount: Int) {
        charges += amount
    }

    override operator fun minusAssign(amount: Int) {
        charges -= amount
    }
}