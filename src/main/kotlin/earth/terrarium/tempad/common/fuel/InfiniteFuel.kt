package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.api.fuel.FuelHandler

object InfiniteFuel: FuelHandler {
    override val charges: Int = 1
    override val totalCharges: Int = 1
    override val hasCharges: Boolean = true
    override val hasSpaceLeft: Boolean = false

    override fun minusAssign(amount: Int) {}
    override fun plusAssign(amount: Int) {}
}