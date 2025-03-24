package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec

data class PortalPlacementComponent(val x: Float, val y: Float, val z: Float, val angle: Float, val isVertical: Boolean) {
    companion object {
        val codec: Codec<PortalPlacementComponent> = RecordCodecBuilder.create { it ->
            it.group(
                Codec.FLOAT.fieldOf("x").forGetter { it.x },
                Codec.FLOAT.fieldOf("y").forGetter { it.y },
                Codec.FLOAT.fieldOf("z").forGetter { it.z },
                Codec.FLOAT.fieldOf("angle").forGetter { it.angle },
                Codec.BOOL.fieldOf("isVertical").forGetter { it.isVertical }
            ).apply(it, ::PortalPlacementComponent)
        }

        val byteCodec: ByteCodec<PortalPlacementComponent> = ObjectByteCodec.create(
            ByteCodec.FLOAT.fieldOf { it.x },
            ByteCodec.FLOAT.fieldOf { it.y },
            ByteCodec.FLOAT.fieldOf { it.z },
            ByteCodec.FLOAT.fieldOf { it.angle },
            ByteCodec.BOOLEAN.fieldOf { it.isVertical },
            ::PortalPlacementComponent,
        )
    }
}