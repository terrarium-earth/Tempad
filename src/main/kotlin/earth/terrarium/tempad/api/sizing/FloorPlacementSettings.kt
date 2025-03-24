package earth.terrarium.tempad.api.sizing

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.tempadId
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.phys.Vec3

class FloorPlacementSettings(val xOffset: Float, val yOffset: Float, val zOffset: Float): TimedoorPlacementSettings {
    companion object {
        val type = SizingType("floor".tempadId, ObjectByteCodec.create(
            ByteCodec.FLOAT.fieldOf { it.xOffset },
            ByteCodec.FLOAT.fieldOf { it.yOffset },
            ByteCodec.FLOAT.fieldOf { it.zOffset },
            ::FloorPlacementSettings
        ))

        val width: Float = 20 / 16f
        val height: Float = 4 / 16f
        val depth: Float = 20 / 16f
    }

    constructor() : this(0.0f, 0.0f, 0.0f)

    override val dimensions: EntityDimensions = EntityDimensions.fixed(width, height)
    override val showLineAnimation: Boolean = false
    override val type: SizingType<*> = Companion.type

    override fun widthAtPercent(percent: Float): Float {
        if (percent < 0.5) return width * percent * 2
        return width
    }

    override fun heightAtPercent(percent: Float): Float = height

    override fun depthAtPercent(percent: Float): Float {
        if (percent > 0.5) return Mth.lerp((percent - 0.5f) * 2, 0.2f, depth)
        return 0.2f
    }

    override fun placeTimedoor(type: DoorType, anchor: Vec3, angle: Float, timedoor: TimedoorEntity) {
        when (type) {
            DoorType.ENTRY -> timedoor.setPos(anchor.x + xOffset, anchor.y + yOffset, anchor.z + zOffset)
            DoorType.EXIT -> timedoor.setPos(anchor.x, anchor.y + 2.4f, anchor.z)
        }
    }

    override fun TimedoorEntity.isInside(entity: Entity) = true
}