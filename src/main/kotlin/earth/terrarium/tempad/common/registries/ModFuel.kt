package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.FuelRegistry
import earth.terrarium.tempad.common.fuel.*

object ModFuel {
    val energy = FuelRegistry.register("energy".tempadId) { stack, charges -> EnergyFuelHandler(stack, charges) }
    val solid = FuelRegistry.register("solid".tempadId) { stack, charges -> SolidFuelHandler(stack, charges) }
    val liquid = FuelRegistry.register("liquid".tempadId) { stack, charges -> LiquidFuelHandler(stack, charges) }
    val experience = FuelRegistry.register("experience".tempadId) { stack, charges -> ExperienceFuelHandler(stack, charges)}

    val infinite = FuelRegistry.register("none".tempadId) { _, _ -> InfiniteFuel }
    val empty = FuelRegistry.register("empty".tempadId) { _, _ -> EmptyFuel }

    fun init() {}
}