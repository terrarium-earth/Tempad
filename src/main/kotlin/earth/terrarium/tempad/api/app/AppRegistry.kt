package earth.terrarium.tempad.api.app

import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

typealias AppProvider = (slotId: Int) -> TempadApp<*>

object AppRegistry {
    private val apps = mutableMapOf<ResourceLocation, AppProvider>()

    fun register(id: ResourceLocation, provider: AppProvider) {
        apps[id] = provider
    }

    fun get(id: ResourceLocation, slotId: Int): TempadApp<*>? {
        return apps[id]?.invoke(slotId)
    }

    fun getAll(slotId: Int): Map<ResourceLocation, TempadApp<*>> {
        return apps.mapValues { it.value(slotId) }
    }
}