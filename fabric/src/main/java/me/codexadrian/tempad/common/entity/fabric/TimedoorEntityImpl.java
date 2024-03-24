package me.codexadrian.tempad.common.entity.fabric;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

public class TimedoorEntityImpl {
    public static void teleportEntity(ServerLevel destinationLevel, Vec3 pos, Vec3 deltaMovement, int angle, Entity entity) {
        FabricDimensions.teleport(entity, destinationLevel, new PortalInfo(pos, deltaMovement, angle, entity.getXRot()));
    }
}
