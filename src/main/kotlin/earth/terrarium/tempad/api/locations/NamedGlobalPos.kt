package earth.terrarium.tempad.api.locations

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus

interface NamedGlobalPos {
    val name: Component
    val pos: Vec3?
    val dimension: ResourceKey<Level>?
    val angle: Float
    val color: Color

    val display: List<Component>
    val type: LocationType<*>

    companion object {
        val codec: Codec<NamedGlobalPos> = LocationType.codec.dispatch({ it.type }, { it.codec })
        val byteCodec: ByteCodec<NamedGlobalPos> = LocationType.byteCodec.dispatch({ it.byteCodec as ByteCodec<NamedGlobalPos> }, { it.type })
    }
}

fun offsetLocation(pos: Vec3, angle: Float, distance: Int = 1): Vec3 {
    val angleInRadians = (angle + 90) * Mth.DEG_TO_RAD
    return pos + Vec3(Mth.cos(angleInRadians).toDouble() * distance, 0.0, Mth.sin(angleInRadians).toDouble() * distance)
}

val NamedGlobalPos.offsetLocation get() = pos?.let { offsetLocation(it, angle + 180) }

data class ClientDisplay(val name: Component, val info: List<Component>) {
    companion object {
        val byteCodec: ByteCodec<ClientDisplay> = ObjectByteCodec.create(
            ExtraByteCodecs.COMPONENT.fieldOf { it.name },
            ExtraByteCodecs.COMPONENT.listOf().fieldOf { it.info },
            ::ClientDisplay
        )
    }
}

val NamedGlobalPos.clientDisplay: ClientDisplay get() = ClientDisplay(name, display)

data class LocationType<T: NamedGlobalPos>(val id: ResourceLocation, val byteCodec: ByteCodec<T>, val codec: MapCodec<T>) {
    companion object {
        val codec = ResourceLocation.CODEC.comapFlatMap(LocationTypeRegistry::decode, LocationType<*>::id)
        val byteCodec = ExtraByteCodecs.RESOURCE_LOCATION.map(LocationTypeRegistry::get, LocationType<*>::id)
    }
}

object LocationTypeRegistry {
    val registry: Map<ResourceLocation, LocationType<*>>
        field = mutableMapOf()

    fun register(type: LocationType<*>) {
        registry[type.id] = type
    }

    fun get(id: ResourceLocation): LocationType<*> {
        return registry[id] ?: error("Location type with id $id does not exist.")
    }

    fun decode(id: ResourceLocation): DataResult<out LocationType<*>> {
        return registry[id]?.let { DataResult.success(it) } ?: DataResult.error { "Location type with id $id does not exist." }
    }
}
