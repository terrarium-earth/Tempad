package earth.terrarium.tempad.api.app

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

typealias AppProvider = (player: Player, slotId: Int) -> TempadApp<*>?

data class AppHolder(val id: ResourceLocation, val slotId: Int) {
    fun getApp(player: Player): TempadApp<*>? {
        return AppRegistry.get(id, player, slotId)
    }
}

object AppRegistry {
    private val apps = mutableMapOf<ResourceLocation, AppProvider>()

    val BYTE_CODEC = ObjectByteCodec.create(
        ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.id },
        ByteCodec.INT.fieldOf { it.slotId },
        ::AppHolder
    )

    @JvmName("register")
    operator fun set(id: ResourceLocation, provider: AppProvider) {
        apps[id] = provider
    }

    operator fun get(id: ResourceLocation, player: Player, slotId: Int): TempadApp<*>? {
        return apps[id]?.invoke(player, slotId)
    }

    fun getAll(player: Player, slotId: Int): Map<ResourceLocation, TempadApp<*>> {
        return apps.mapValues { it.value(player, slotId) }.mapNotNull { it.value?.let { app -> it.key to app } }.toMap()
    }

    fun getIds(): Set<ResourceLocation> {
        return apps.keys
    }
}