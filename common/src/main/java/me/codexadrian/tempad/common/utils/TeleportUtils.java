package me.codexadrian.tempad.common.utils;

import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.TempadConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TeleportUtils {

    public static boolean mayTeleport(Level level, Player player) {
        if (player.isCreative()) return true;
        if (level.dimensionTypeRegistration().is(Tempad.TEMPAD_DIMENSION_BLACKLIST)) return false;
        return level.dimension() == player.level().dimension() || TempadConfig.allowInterdimensionalTravel;
    }
}
