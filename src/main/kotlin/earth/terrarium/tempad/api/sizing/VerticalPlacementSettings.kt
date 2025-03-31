package earth.terrarium.tempad.api.sizing

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.locations.offsetLocation
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.tempadId
import net.minecraft.world.phys.Vec3

class VerticalPlacementSettings(val xOffset: Float, val yOffset: Float, val zOffset: Float): DynamicAngledPlacement() {
    companion object {
        val type = SizingType("vertical".tempadId, ObjectByteCodec.create(
            ByteCodec.FLOAT.fieldOf { it.xOffset },
            ByteCodec.FLOAT.fieldOf { it.yOffset },
            ByteCodec.FLOAT.fieldOf { it.zOffset },
            ::VerticalPlacementSettings
        ), RecordCodecBuilder.mapCodec {
            it.group(
                Codec.FLOAT.fieldOf("x").forGetter { it.yOffset },
                Codec.FLOAT.fieldOf("y").forGetter { it.yOffset },
                Codec.FLOAT.fieldOf("z").forGetter { it.yOffset }
            ).apply(it, ::VerticalPlacementSettings)
        })
    }

    override val type: SizingType<*>
        get() = Companion.type

    override fun placeTimedoor(
        type: DoorType,
        anchor: Vec3,
        angle: Float,
        timedoor: TimedoorEntity,
    ) {
        if (type == DoorType.ENTRY) {
            timedoor.setPos(anchor.x + xOffset, anchor.y + yOffset, anchor.z + zOffset)
        } else {
            val offset = offsetLocation(anchor, angle, 1)
            timedoor.setPos(offset.x, anchor.y, offset.z)
        }
        timedoor.yRot = angle
    }
}