package earth.terrarium.tempad.common.utils

import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import com.teamresourceful.resourcefullib.common.color.Color
import net.minecraft.core.UUIDUtil
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.fluids.crafting.FluidIngredient
import java.text.DateFormat
import java.text.SimpleDateFormat
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

val FLUID_INGREDIENT_BYTE_CODEC: ByteCodec<FluidIngredient> = StreamCodecByteCodec.ofRegistry(FluidIngredient.STREAM_CODEC)

val DATE_CODEC: Codec<Date> = Codec.STRING.xmap(String::toLong, Long::toString).xmap(::Date, Date::getTime)

val DATE_BYTE_CODEC: ByteCodec<Date> = ByteCodec.LONG.map(::Date, Date::getTime)

val GAME_PROFILE_BYTE_CODEC: ByteCodec<GameProfile> = ObjectByteCodec.create(
    ByteCodec.UUID.fieldOf { it.id },
    ByteCodec.STRING.fieldOf { it.name },
    ::GameProfile
)

val GAME_PROFILE_CODEC: MapCodec<GameProfile> = RecordCodecBuilder.mapCodec {
    it.group(
        UUIDUtil.CODEC.fieldOf("id").forGetter { it.id },
        Codec.STRING.fieldOf("name").forGetter { it.name },
    ).apply(it, ::GameProfile)
}