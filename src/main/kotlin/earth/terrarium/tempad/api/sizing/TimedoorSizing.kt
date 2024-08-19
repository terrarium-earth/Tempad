package earth.terrarium.tempad.api.sizing

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.common.entity.TimedoorEntity
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.phys.Vec3

interface TimedoorSizing {
    val dimensions: EntityDimensions
    val showLineAnimation: Boolean
    val type: SizingType<*>

    fun widthAtPercent(percent: Float): Float
    fun heightAtPercent(percent: Float): Float
    fun depthAtPercent(percent: Float): Float

    fun placeTimedoor(type: DoorType, anchor: Vec3, angle: Float, timedoor: TimedoorEntity)

    fun TimedoorEntity.isInside(entity: Entity): Boolean

    companion object {
        val codec: ByteCodec<TimedoorSizing> = SizingType.codec.dispatch({ it.codec as ByteCodec<TimedoorSizing> }, { it.type })
    }
}

enum class DoorType {
    ENTRY, EXIT
}

data class SizingType<T: TimedoorSizing>(val id: ResourceLocation, val codec: ByteCodec<T>) {
    companion object {
        val codec: ByteCodec<SizingType<*>> = ExtraByteCodecs.RESOURCE_LOCATION.map(SizingRegistry::get, SizingType<*>::id)
    }
}

object SizingRegistry {
    val sizings: Map<ResourceLocation, SizingType<*>>
        field = mutableMapOf()

    fun <T: TimedoorSizing> register(type: SizingType<T>) {
        sizings[type.id] = type
    }

    fun get(id: ResourceLocation): SizingType<*> {
        return sizings[id] ?: error("Unknown sizing type: $id")
    }
}