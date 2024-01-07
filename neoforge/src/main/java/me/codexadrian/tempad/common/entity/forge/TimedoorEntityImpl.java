package me.codexadrian.tempad.common.entity.forge;

import me.codexadrian.tempad.common.forge.TempadTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TimedoorEntityImpl {
    public static void teleportEntity(ServerLevel destinationLevel, BlockPos pos, Vec3 deltaMovement, Entity entity) {
        entity.changeDimension(destinationLevel, new TempadTeleporter(pos));
    }
}
