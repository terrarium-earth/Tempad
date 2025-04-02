package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import net.minecraft.core.Direction
import org.joml.Vector3f

data class PortalPlacementComponent(val leftRight: Float, val upDown: Float, val forwardBack: Float, val angle: Int, val isUpright: Boolean) {
    companion object {
        val codec: Codec<PortalPlacementComponent> = RecordCodecBuilder.create { it ->
            it.group(
                Codec.FLOAT.fieldOf("leftRight").forGetter { it.leftRight },
                Codec.FLOAT.fieldOf("upDown").forGetter { it.upDown },
                Codec.FLOAT.fieldOf("forwardBack").forGetter { it.forwardBack },
                Codec.INT.fieldOf("angle").forGetter { it.angle },
                Codec.BOOL.fieldOf("isUpright").forGetter { it.isUpright }
            ).apply(it, ::PortalPlacementComponent)
        }

        val byteCodec: ByteCodec<PortalPlacementComponent> = ObjectByteCodec.create(
            ByteCodec.FLOAT.fieldOf { it.leftRight },
            ByteCodec.FLOAT.fieldOf { it.upDown },
            ByteCodec.FLOAT.fieldOf { it.forwardBack },
            ByteCodec.INT.fieldOf { it.angle },
            ByteCodec.BOOLEAN.fieldOf { it.isUpright },
            ::PortalPlacementComponent,
        )
    }

    fun calcOffset(dir: Direction): Vector3f {
        val x = when (dir) {
            Direction.NORTH -> leftRight
            Direction.SOUTH -> -leftRight
            Direction.WEST -> -forwardBack
            Direction.EAST -> forwardBack
            else -> error("Unsupported direction $dir")
        }
        val z = when (dir) {
            Direction.NORTH -> -forwardBack
            Direction.SOUTH -> forwardBack
            Direction.WEST -> -leftRight
            Direction.EAST -> leftRight
            else -> error("Unsupported direction $dir")
        }
        return Vector3f(x, upDown, z)
    }
}