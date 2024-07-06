package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.api.fuel.FuelHandler

object EmptyFuel: FuelHandler {
    override val charges: Int = 0
    override val totalCharges: Int = 1
    override val hasCharges: Boolean = false
    override val hasSpaceLeft: Boolean = false

    override fun minusAssign(amount: Int) {}
    override fun plusAssign(amount: Int) {}
}