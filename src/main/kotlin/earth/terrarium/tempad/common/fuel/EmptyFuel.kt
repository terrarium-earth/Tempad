package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.api.fuel.FuelConsumer

object EmptyFuel: FuelConsumer {
    override val charges: Int = 0
    override val totalCharges: Int = 1
    override val hasCharges: Boolean = false

    override fun minusAssign(amount: Int) {}
}