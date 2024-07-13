package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.FuelHandler
import net.minecraft.resources.ResourceLocation

object EmptyFuel: FuelHandler {
    override val id: ResourceLocation = "empty".tempadId
    override val charges: Int = 0
    override val totalCharges: Int = 1
    override val hasCharges: Boolean = false
    override val hasSpaceLeft: Boolean = false

    override fun minusAssign(amount: Int) {}
}