package earth.terrarium.tempad.api.sizing

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
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
        ))
    }

    override val type: SizingType<*>
        get() = Companion.type

    override fun placeTimedoor(
        type: DoorType,
        anchor: Vec3,
        angle: Float,
        timedoor: TimedoorEntity,
    ) {
        timedoor.setPos(anchor.x + xOffset, anchor.y + yOffset, anchor.z + zOffset)
        timedoor.yRot = angle
    }
}