package me.codexadrian.tempad.common.utils.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformUtilsImpl {
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
