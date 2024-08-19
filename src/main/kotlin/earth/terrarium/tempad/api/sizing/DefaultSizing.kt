package earth.terrarium.tempad.api.sizing

import com.teamresourceful.bytecodecs.base.ByteCodec
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
        override val dimensions: EntityDimensions = EntityDimensions.fixed(width / 16f, height / 16f)

        override fun widthAtPercent(percent: Float): Float {
            if (percent < 0.5) return width * percent * 2
            return width
        }

        override fun heightAtPercent(percent: Float): Float {
            if (percent > 0.5) return height * (percent - 0.5f) * 2
            return height
        }

        override fun depthAtPercent(percent: Float): Float = depth

        override fun placeTimedoor(pos: Vec3, angle: Float, timedoor: TimedoorEntity) {
            TODO("Not yet implemented")
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
            if (percent > 0.5) return depth * (percent - 0.5f) * 2
            return depth
        }

        override fun placeTimedoor(pos: Vec3, angle: Float, timedoor: TimedoorEntity) {
        }

        override fun TimedoorEntity.isInside(entity: Entity) = true
    }
}