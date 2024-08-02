package earth.terrarium.tempad.common.utils

import com.teamresourceful.resourcefullib.common.fluid.data.FluidData
import com.teamresourceful.resourcefullib.common.fluid.data.FluidProperties
import com.teamresourceful.resourcefullib.common.fluid.data.FluidSounds
import com.teamresourceful.resourcefullib.common.fluid.data.ImmutableFluidProperties
import com.teamresourceful.resourcefullib.common.fluid.registry.ResourcefulFluidRegistry
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.pathfinder.PathType


class FluidPropertyBuilder {
    var motionScale = 0.014
    var canPushEntity = true
    var canSwim = false
    var canDrown = true
    var fallDistanceModifier = 0.5f
    var canExtinguish = false
    var canConvertToSource = true
    var supportsBoating = false
    var pathType = PathType.WATER
    var adjacentPathType = PathType.WATER_BORDER
    var canHydrate = true
    var lightLevel = 0
    var density = 1000
    var temperature = 300
    var viscosity = 1000
    var rarity = Rarity.COMMON
    var sounds = FluidSounds()
    var still: ResourceLocation? = null
    var flowing: ResourceLocation? = null
    var overlay: ResourceLocation? = null
    var screenOverlay: ResourceLocation? = null
    var tintColor = -1
    var tickRate = 5
    var slopeFindDistance = 4
    var dropOff = 1
    var explosionResistance = 100.0f
    var canPlace = true

    fun build(): FluidProperties = ImmutableFluidProperties(
        motionScale,
        canPushEntity,
        canSwim,
        canDrown,
        fallDistanceModifier,
        canExtinguish,
        canConvertToSource,
        supportsBoating,
        pathType,
        adjacentPathType,
        canHydrate,
        lightLevel,
        density,
        temperature,
        viscosity,
        rarity,
        sounds,
        still,
        flowing,
        overlay,
        screenOverlay,
        tintColor,
        tickRate,
        slopeFindDistance,
        dropOff,
        explosionResistance,
        canPlace
    )
}

fun ResourcefulFluidRegistry.registerProps(name: String, block: FluidPropertyBuilder.() -> Unit): RegistryEntry<FluidData> {
    val builder = FluidPropertyBuilder()
    builder.block()
    return this.register(name, builder.build())
}
