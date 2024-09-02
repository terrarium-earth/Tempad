package earth.terrarium.tempad.api.locations

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.COLOR_BYTE_CODEC
import earth.terrarium.tempad.common.utils.VEC3_BYTE_CODEC
import earth.terrarium.tempad.common.utils.translatable
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus

data class NamedGlobalVec3(val name: Component, val pos: Vec3, val dimension: ResourceKey<Level>, val angle: Float, val color: Color): TooltipComponent {
    companion object {
        val CODEC: MapCodec<NamedGlobalVec3> = RecordCodecBuilder.mapCodec {
            it.group(
                ComponentSerialization.CODEC.fieldOf("name").forGetter { it.name },
                Vec3.CODEC.fieldOf("pos").forGetter { it.pos },
                Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter { it.dimension },
                Codec.FLOAT.fieldOf("angle").forGetter { it.angle },
                Color.CODEC.fieldOf("color").forGetter(NamedGlobalVec3::color),
            ).apply(it, ::NamedGlobalVec3)
        }

        val BYTE_CODEC: ByteCodec<NamedGlobalVec3> = ObjectByteCodec.create(
            ExtraByteCodecs.COMPONENT.fieldOf { it.name },
            VEC3_BYTE_CODEC.fieldOf { it.pos },
            ExtraByteCodecs.DIMENSION.fieldOf { it.dimension },
            ByteCodec.FLOAT.fieldOf { it.angle },
            COLOR_BYTE_CODEC.fieldOf { it.color },
            ::NamedGlobalVec3
        )
    }

    val offsetLocation = offsetLocation(pos, angle + 180)

    val x: Int = pos.x.toInt()
    val y: Int = pos.y.toInt()
    val z: Int = pos.z.toInt()
    val dimensionText = Component.translatable(dimension.location().toLanguageKey("dimension"))
}

fun offsetLocation(pos: Vec3, angle: Float, distance: Int = 1): Vec3 {
    val angleInRadians = (angle + 90) * Mth.DEG_TO_RAD
    return pos + Vec3(Mth.cos(angleInRadians).toDouble() * distance, 0.0, Mth.sin(angleInRadians).toDouble() * distance)
}

val Player.namedGlobalVec3: NamedGlobalVec3 get() = NamedGlobalVec3(gameProfile.name.translatable, position(), level().dimension(), yRot, Tempad.ORANGE)