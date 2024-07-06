package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.data.fuelCharges
import earth.terrarium.tempad.common.recipe.SingleFluidRecipeInput
import earth.terrarium.tempad.common.registries.ModFuel
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.ModRecipes
import earth.terrarium.tempad.common.registries.fuelType
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.set
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import kotlin.jvm.optionals.getOrNull

enum class DefaultFuelType(val consumer: (container: SimpleContainer, player: Player, tempad: ItemStack) -> Unit) {
    SOLID(consumer = { container, player, tempad ->
        val input = SingleRecipeInput(container.getItem(0))
        val recipe = player.level().recipeManager.getRecipeFor(ModRecipes.SOLID_FUEL_TYPE, input, player.level()).getOrNull()
        recipe?.value()?.let {
            container.removeItem(0, it.count)
            tempad.fuelCharges += 1
        }
    }),

    LIQUID(consumer = consumer@ { container, player, tempad ->
        val handler = container[0][Capabilities.FluidHandler.ITEM] ?: return@consumer
        val fluid = handler.getFluidInTank(0)
        if (fluid.isEmpty) return@consumer
        val input = SingleFluidRecipeInput(fluid)
        val recipe = player.level().recipeManager.getRecipeFor(ModRecipes.LIQUID_FUEL_TYPE, input, player.level()).getOrNull()
        recipe?.value()?.let {
            val drained = handler.drain(fluid.copyWithAmount(it.amount), FluidAction.SIMULATE)
            if (drained.amount < it.amount) return@consumer
            handler.drain(fluid.copyWithAmount(it.amount), FluidAction.EXECUTE)
            tempad.fuelCharges += 1
            container[0] = handler.container
        }
    }),

    EXPERIENCE(consumer = consumer@ { container, player, tempad ->
        //TODO Add support for modded containers holding experience
        return@consumer
    }),

    ENERGY(consumer = consumer@ { container, player, tempad ->
        val handler = container[0][Capabilities.EnergyStorage.ITEM] ?: return@consumer
        if (handler.energyStored < CommonConfigCache.energyPerCharge) return@consumer
        val extracted = handler.extractEnergy(CommonConfigCache.energyPerCharge, true)
        if (extracted < CommonConfigCache.energyPerCharge) return@consumer
        handler.extractEnergy(CommonConfigCache.energyPerCharge, false)
    }),

    NONE(consumer = { _, _, _ -> });

    companion object {
        fun parse(stack: ItemStack): DefaultFuelType {
            var type = stack.fuelType
            if (type == null) {
                if (stack.item === ModItems.TEMPAD) {
                    type = ResourceLocation.tryParse(CommonConfigCache.Tempad.fuelType)
                } else if (stack.item === ModItems.ADVANCED_TEMPAD) {
                    type = ResourceLocation.tryParse(CommonConfigCache.AdvancedTempad.fuelType)
                }
            }
            if (type == null) {
                return NONE
            }
            return when (type) {
                ModFuel.SOLID -> SOLID
                ModFuel.LIQUID -> LIQUID
                ModFuel.EXPERIENCE -> EXPERIENCE
                ModFuel.ENERGY -> ENERGY
                else -> NONE
            }
        }
    }

    fun acceptsFuel() = this != NONE

    fun consume(container: SimpleContainer, player: Player, tempad: ItemStack) {
        consumer(container, player, tempad)
    }
}