package earth.terrarium.tempad.api.fuel

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

typealias FuelProvider = (tempadItem: ItemStack, totalCharges: Int) -> FuelHandler

object FuelRegistry {
    private val providers = mutableMapOf<ResourceLocation, FuelProvider>()

    @JvmStatic
    fun register(id: ResourceLocation, provider: FuelProvider): ResourceLocation {
        providers[id] = provider
        return id
    }

    @JvmStatic
    fun get(id: ResourceLocation, tempadItem: ItemStack, totalCharges: Int): FuelHandler? {
        return providers[id]?.invoke(tempadItem, totalCharges)
    }
}