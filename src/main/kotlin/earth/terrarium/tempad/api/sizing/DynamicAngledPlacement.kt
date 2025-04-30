package earth.terrarium.tempad.api.sizing

import com.mojang.serialization.MapCodec
import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.api.locations.offsetLocation
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.tempadId
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.phys.Vec3

open class DynamicAngledPlacement: TimedoorPlacementSettings {
    companion object {
        val type = SizingType("default".tempadId, ByteCodec.unit(::DynamicAngledPlacement), MapCodec.unit(::DynamicAngledPlacement))
    }

    val width: Float = 20 / 16f
    val height: Float = 36 / 16f
    val depth: Float = 6 / 16f

    override val showLineAnimation: Boolean = true
    override val type: SizingType<*> = Companion.type
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
        timedoor.yRot = angle + if (type == DoorType.ENTRY) 180 else 0
    }

    override fun TimedoorEntity.isInside(entity: Entity): Boolean {
        val hypotenuse = (entity.x - x) * (entity.x - x) + (entity.z - z) * (entity.z - z)
        val alpha = Mth.atan2((entity.z - z), (entity.x - x)).toFloat()
        val theta = Mth.sin(alpha - yRot * Mth.DEG_TO_RAD)
        val maxDistance = (depth / 2) + entity.bbWidth / 2f
        return theta * theta * hypotenuse < maxDistance * maxDistance
    }
}