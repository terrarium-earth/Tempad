package earth.terrarium.tempad.api.sizing

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.common.entity.TimedoorEntity
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.phys.Vec3

interface TimedoorPlacementSettings {
    val dimensions: EntityDimensions
    val showLineAnimation: Boolean
    val type: SizingType<*>

    fun widthAtPercent(percent: Float): Float
    fun heightAtPercent(percent: Float): Float
    fun depthAtPercent(percent: Float): Float

    fun placeTimedoor(type: DoorType, anchor: Vec3, angle: Float, timedoor: TimedoorEntity)

    fun TimedoorEntity.isInside(entity: Entity): Boolean

    companion object {
        val byteCodec: ByteCodec<TimedoorPlacementSettings> = SizingType.byteCodec.dispatch({ it.byteCodec as ByteCodec<TimedoorPlacementSettings> }, { it.type })
        val codec: Codec<TimedoorPlacementSettings> = SizingType.codec.dispatch({ it.type }) { it.codec }
    }
}

enum class DoorType {
    ENTRY, EXIT
}

data class SizingType<T: TimedoorPlacementSettings>(val id: Identifier, val byteCodec: ByteCodec<T>, val codec: MapCodec<T>) {
    companion object {
        val byteCodec: ByteCodec<SizingType<*>> = ExtraByteCodecs.RESOURCE_LOCATION.map(SizingRegistry::get, SizingType<*>::id)
        val codec: Codec<SizingType<*>> = Identifier.CODEC.comapFlatMap(SizingRegistry::decode, SizingType<*>::id)
    }
}

object SizingRegistry {
    val sizings: Map<Identifier, SizingType<*>>
        field = mutableMapOf()

    fun <T: TimedoorPlacementSettings> register(type: SizingType<T>) {
        sizings[type.id] = type
    }

    fun get(id: Identifier): SizingType<*> {
        return sizings[id] ?: error("Unknown sizing type: $id")
    }

    fun decode(id: Identifier): DataResult<out SizingType<*>> {
        return sizings[id]?.let { DataResult.success(it) } ?: DataResult.error { "Sizing type with id $id does not exist." }
    }
}