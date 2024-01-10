package me.codexadrian.tempad.common.utils.forge;


import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformUtilsImpl {
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
