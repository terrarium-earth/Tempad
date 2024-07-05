package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.FuelRegistry
import earth.terrarium.tempad.common.fuel.*

object ModFuel {
    val ENERGY = FuelRegistry.register("energy".tempadId) { stack, charges -> EnergyFuelConsumer(stack, charges) }
    val SOLID = FuelRegistry.register("solid".tempadId) { stack, charges -> SolidFuelConsumer(stack, charges) }
    val EXPERIENCE = FuelRegistry.register("experience".tempadId) { stack, charges -> ExperienceFuelConsumer(stack, charges)}

    val INFINITE = FuelRegistry.register("none".tempadId) { _, _ -> InfiniteFuel }
    val EMPTY = FuelRegistry.register("empty".tempadId) { _, _ -> EmptyFuel }

    fun init() {}
}