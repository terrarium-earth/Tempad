package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.sizing.DoorType
import earth.terrarium.tempad.api.sizing.SizingType
import earth.terrarium.tempad.api.sizing.TimedoorPlacementSettings
import earth.terrarium.tempad.common.entity.TimedoorEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.phys.Vec3

class WorkstationTimedoorConfig(
    override val dimensions: EntityDimensions,
    override val showLineAnimation: Boolean,
    override val type: SizingType<*>
) : TimedoorPlacementSettings {
    override fun widthAtPercent(percent: Float): Float {
        TODO("Not yet implemented")
    }

    override fun heightAtPercent(percent: Float): Float {
        TODO("Not yet implemented")
    }

    override fun depthAtPercent(percent: Float): Float {
        TODO("Not yet implemented")
    }

    override fun placeTimedoor(
        type: DoorType,
        anchor: Vec3,
        angle: Float,
        timedoor: TimedoorEntity,
    ) {
        TODO("Not yet implemented")
    }

    override fun TimedoorEntity.isInside(entity: Entity): Boolean {
        TODO("Not yet implemented")
    }
}