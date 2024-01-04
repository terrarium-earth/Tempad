package me.codexadrian.tempad.common.utils.forge;

import net.minecraftforge.fml.ModList;

public class TeleportUtilsImpl {
    public static boolean isBaubleModLoaded() {
        return ModList.get().isLoaded("curios");
    }
}
