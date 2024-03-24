package me.codexadrian.tempad.api.teams;

import net.minecraft.world.level.Level;

import java.util.UUID;

public interface TeamConfig {
    boolean test(Level level, UUID owner, UUID member);
}
