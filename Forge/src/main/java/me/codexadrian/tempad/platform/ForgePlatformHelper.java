package me.codexadrian.tempad.platform;

import me.codexadrian.tempad.TempadTeleporter;
import me.codexadrian.tempad.platform.services.IPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void teleportEntity(ServerLevel destinationLevel, BlockPos pos, Vec3 deltaMovement, Entity entity) {
        entity.changeDimension(destinationLevel, new TempadTeleporter(pos));
    }
}
