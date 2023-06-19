package me.codexadrian.tempad.common.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.NotImplementedException;

import java.nio.file.Path;

public class PlatformUtils {

    @ExpectPlatform
    public static Path getConfigDir(){
        throw new NotImplementedException();
    }
}
