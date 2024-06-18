package earth.terrarium.tempad.common.entity

import earth.terrarium.tempad.common.data.LocationData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.portal.PortalInfo
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.util.ITeleporter
import java.util.function.Function

data class TimedoorTeleporter(val locationData: LocationData, val deltaMovement: Vec3): ITeleporter {
    override fun placeEntity(
        entity: Entity,
        currentWorld: ServerLevel,
        destWorld: ServerLevel,
        yaw: Float,
        repositionEntity: Function<Boolean, Entity>
    ): Entity {
        return repositionEntity.apply(false)
    }

    override fun getPortalInfo(
        entity: Entity,
        destWorld: ServerLevel,
        defaultPortalInfo: Function<ServerLevel, PortalInfo>
    ): PortalInfo {
        return PortalInfo(locationData.globalPos.pos.center, deltaMovement, locationData.angle, entity.xRot)
    }

    override fun isVanilla() = false
}