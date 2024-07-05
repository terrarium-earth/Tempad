package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.api.fuel.FuelConsumer

object InfiniteFuel: FuelConsumer {
    override val charges: Int = 1
    override val totalCharges: Int = 1
    override val hasCharges: Boolean = true
    override fun minusAssign(amount: Int) {}
}