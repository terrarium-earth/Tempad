package earth.terrarium.tempad.api.locations

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.common.utils.COLOR_BYTE_CODEC
import earth.terrarium.tempad.common.utils.VEC3_BYTE_CODEC
import earth.terrarium.tempad.common.utils.byteCodec
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus

data class LocationType<T: NamedGlobalLocation>(val id: ResourceLocation, val byteCodec: ByteCodec<T>, val codec: Codec<T>)

interface NamedGlobalLocation {
    val name: String
    val pos: Vec3
    val dimension: ResourceKey<Level>
    val angle: Float
    val color: Color

    val display: List<Component>
    val type: LocationType<*>
}

object LocationTypeRegistry {

}

data class LocationData(val name: String, val pos: Vec3, val dimension: ResourceKey<Level>, val angle: Float, val color: Color) {
    companion object {
        val MAP_CODEC: MapCodec<LocationData> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.STRING.fieldOf("name").forGetter { it.name },
                Vec3.CODEC.fieldOf("pos").forGetter { it.pos },
                Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter { it.dimension },
                Codec.FLOAT.fieldOf("angle").forGetter { it.angle },
                Color.CODEC.fieldOf("color").forGetter(LocationData::color),
            ).apply(it, ::LocationData)
        }

        val CODEC: Codec<LocationData> = MAP_CODEC.codec()

        val BYTE_CODEC: ByteCodec<LocationData> = ObjectByteCodec.create(
            ByteCodec.STRING.fieldOf { it.name },
            VEC3_BYTE_CODEC.fieldOf { it.pos },
            ExtraByteCodecs.DIMENSION.fieldOf { it.dimension },
            ByteCodec.FLOAT.fieldOf { it.angle },
            COLOR_BYTE_CODEC.fieldOf { it.color },
            ::LocationData
        )

        fun offsetLocation(pos: Vec3, angle: Float, distance: Int = 1): Vec3 {
            val angleInRadians = (angle + 90) * Mth.DEG_TO_RAD
            return pos + Vec3(Mth.cos(angleInRadians).toDouble() * distance, 0.0, Mth.sin(angleInRadians).toDouble() * distance)
        }
    }

    val offsetLocation = offsetLocation(pos, angle + 180)

    val x: Int = pos.x.toInt()
    val y: Int = pos.y.toInt()
    val z: Int = pos.z.toInt()
    val dimensionText = Component.translatable(dimension.location().toLanguageKey("dimension"))

    val dimComponent: Component = Component.translatable(dimension.location().toLanguageKey("dimension"))
}