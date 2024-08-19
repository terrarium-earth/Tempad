package earth.terrarium.tempad.api.sizing

import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.api.locations.offsetLocation
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.tempadId
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.phys.Vec3

enum class DefaultSizing: TimedoorSizing {
    DEFAULT {
        val width: Float = 20 / 16f
        val height: Float = 36 / 16f
        val depth: Float = 6 / 16f
        override val showLineAnimation: Boolean = true
        override val type: SizingType<*> = SizingType("default".tempadId, ByteCodec.unit(DEFAULT))
        override val dimensions: EntityDimensions = EntityDimensions.fixed(width, height)

        override fun widthAtPercent(percent: Float): Float {
            if (percent < 0.5) return width * percent * 2
            return width
        }

        override fun heightAtPercent(percent: Float): Float {
            if (percent > 0.5) return Mth.lerp((percent - 0.5f) * 2, 0.2f, height)
            return 0.2f
        }

        override fun depthAtPercent(percent: Float): Float = depth

        override fun placeTimedoor(type: DoorType, anchor: Vec3, angle: Float, timedoor: TimedoorEntity) {
            val offset = offsetLocation(anchor, angle, if(type == DoorType.ENTRY) CommonConfig.TimeDoor.placementDistance else 1)
            timedoor.setPos(offset.x, anchor.y, offset.z)
            timedoor.yRot = angle
        }

        override fun TimedoorEntity.isInside(entity: Entity): Boolean {
            val hypotenuse = (entity.x - x) * (entity.x - x) + (entity.z - z) * (entity.z - z)
            val alpha = Mth.atan2((entity.z - z), (entity.x - x)).toFloat()
            val theta = Mth.sin(alpha - yRot * Mth.DEG_TO_RAD)
            val maxDistance = (depth / 2) + entity.bbWidth / 2f
            return theta * theta * hypotenuse < maxDistance * maxDistance
        }
    },
    FLOOR {
        val width: Float = 20 / 16f
        val height: Float = 4 / 16f
        val depth: Float = 20 / 16f
        override val dimensions: EntityDimensions = EntityDimensions.fixed(width, height)
        override val showLineAnimation: Boolean = false
        override val type: SizingType<*> = SizingType("floor".tempadId, ByteCodec.unit(FLOOR))

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
                DoorType.ENTRY -> timedoor.setPos(anchor.x, anchor.y, anchor.z)
                DoorType.EXIT -> timedoor.setPos(anchor.x, anchor.y + 2.4f, anchor.z)
            }
        }

        override fun TimedoorEntity.isInside(entity: Entity) = true
    }
}