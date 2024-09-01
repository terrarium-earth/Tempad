package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.fluid.ResourcefulFlowingFluid
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData
import com.teamresourceful.resourcefullib.common.fluid.registry.ResourcefulFluidRegistry
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullib.common.registry.neoforge.NeoForgeResourcefulFluidRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.registerProps
import earth.terrarium.tempad.common.utils.vanillaId
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.material.Fluid

object ModFluids {
    val dataRegistry: ResourcefulFluidRegistry = NeoForgeResourcefulFluidRegistry(Tempad.MOD_ID)
    val registry: ResourcefulRegistry<Fluid> = ResourcefulRegistries.create(BuiltInRegistries.FLUID, Tempad.MOD_ID)

    val chrononData: FluidData by dataRegistry.registerProps("chronon") {
        density = 1000
        viscosity = 1000
        temperature = 300
        canConvertToSource = false
        canPlace = false
    }

    val stillChronon: Fluid by registry.register("chronon") {
        ResourcefulFlowingFluid.Still(chrononData)
    }

    val flowingChronon: Fluid by registry.register("flowing_chronon") {
        ResourcefulFlowingFluid.Flowing(chrononData)
    }
}