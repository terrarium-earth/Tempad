package me.codexadrian.tempad.platform;

import me.codexadrian.tempad.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;


public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void teleportEntity(ServerLevel destinationLevel, BlockPos pos, Vec3 deltaMovement, Entity entity) {
        FabricDimensions.teleport(entity, destinationLevel, new PortalInfo(new Vec3(pos.getX(), pos.getY(), pos.getZ()), deltaMovement, entity.getYRot(), entity.getXRot()));
    }

}
