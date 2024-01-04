package me.codexadrian.tempad.common.utils.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class TeleportUtilsImpl {
    public static boolean isBaubleModLoaded() {
        return FabricLoader.getInstance().isModLoaded("trinkets");
    }
}
