package earth.terrarium.tempad.api.app

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import net.minecraft.resources.ResourceLocation

typealias AppProvider = (slotId: Int) -> TempadApp<*>

data class AppHolder(val id: ResourceLocation, val slotId: Int) {
    val app: TempadApp<*>? = AppRegistry.get(id, slotId)
}

object AppRegistry {
    private val apps = mutableMapOf<ResourceLocation, AppProvider>()

    val BYTE_CODEC = ObjectByteCodec.create(
        ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.id },
        ByteCodec.INT.fieldOf { it.slotId },
        ::AppHolder
    )

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