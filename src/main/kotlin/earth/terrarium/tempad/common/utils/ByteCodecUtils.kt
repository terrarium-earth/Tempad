package earth.terrarium.tempad.common.utils

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import com.teamresourceful.resourcefullib.common.color.Color
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.fluids.crafting.FluidIngredient
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import java.util.*

val COLOR_BYTE_CODEC: ByteCodec<Color> = ByteCodec.BYTE.dispatch(
    { aByte: Byte ->
        when (aByte) {
            0.toByte() -> ByteCodec.unit(Color.DEFAULT)
            1.toByte() -> ByteCodec.unit(Color.RAINBOW)
            else -> ByteCodec.INT.map(::Color, Color::getValue)
        }
    },
    { color: Color ->
        when {
            color.isDefault -> 0.toByte()
            color.isRainbow -> 1.toByte()
            else -> 2.toByte()
        }
    })

val VEC3_BYTE_CODEC: ByteCodec<Vec3> = ObjectByteCodec.create(
    ByteCodec.DOUBLE.fieldOf { it.x },
    ByteCodec.DOUBLE.fieldOf { it.y },
    ByteCodec.DOUBLE.fieldOf { it.z },
    ::Vec3
)

val STRING_UUID_BYTE_CODEC: ByteCodec<UUID> = ByteCodec.STRING.map(UUID::fromString, UUID::toString)

val FLUID_INGREDIENT_BYTE_CODEC: ByteCodec<FluidIngredient> = StreamCodecByteCodec.ofRegistry(FluidIngredient.STREAM_CODEC)