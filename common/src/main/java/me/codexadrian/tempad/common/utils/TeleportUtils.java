package me.codexadrian.tempad.common.utils;

import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.config.TempadConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TeleportUtils {

    public static boolean mayTeleport(ResourceKey<Level> level, Player player) {
        if (player.isCreative()) return true;
        return level.equals(player.level().dimension()) || player.level().isClientSide() ? ConfigCache.allowInterdimensionalTravel : TempadConfig.allowInterdimensionalTravel;
    }
}
