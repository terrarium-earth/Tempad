package me.codexadrian.tempad.common.entity.neoforge;

import me.codexadrian.tempad.common.neoforge.TempadTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TimedoorEntityImpl {
    public static void teleportEntity(ServerLevel destinationLevel, Vec3 pos, Vec3 deltaMovement, int angle, Entity entity) {
        entity.changeDimension(destinationLevel, new TempadTeleporter(pos, deltaMovement, angle));
    }
}
