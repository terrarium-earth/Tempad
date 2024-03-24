package me.codexadrian.tempad.common.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TempadTeleporter implements ITeleporter {
    private final Vec3 position;
    private final Vec3 deltaMovement;
    private final int angle;

    public TempadTeleporter(Vec3 pos, Vec3 deltaMovement, int angle) {
        this.position = pos;
        this.deltaMovement = deltaMovement;
        this.angle = angle;
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(this.position, deltaMovement, angle, entity.getXRot());
    }

    @Override
    public boolean isVanilla() {
        return false;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }
}
