package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.MutableFuelHandler
import earth.terrarium.tempad.common.registries.fuelCharges
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

abstract class BaseFuelHandler(val stack: ItemStack, name: String): MutableFuelHandler {
    override var charges: Int
        get() = stack.fuelCharges
        set(value) {
            stack.fuelCharges = value
        }

    override val id: ResourceLocation = name.tempadId

    override operator fun plusAssign(amount: Int) {
        charges += amount
    }

    override operator fun minusAssign(amount: Int) {
        charges -= amount
    }
}