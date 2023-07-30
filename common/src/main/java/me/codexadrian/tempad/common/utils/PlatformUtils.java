package me.codexadrian.tempad.common.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

import java.nio.file.Path;

public class PlatformUtils {

    @ExpectPlatform
    public static Path getConfigDir() {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modid) {
        throw new NotImplementedException();
    }
}
