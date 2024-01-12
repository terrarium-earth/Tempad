package me.codexadrian.tempad.common.utils.neoforge;

import net.neoforged.fml.ModList;

public class TeleportUtilsImpl {
    public static boolean isBaubleModLoaded() {
        return ModList.get().isLoaded("curios");
    }
}
