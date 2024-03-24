package me.codexadrian.tempad.common.teams;

import me.codexadrian.tempad.api.teams.TeamConfig;
import me.codexadrian.tempad.api.teams.TeamConfigApi;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class SoleTeamConfig implements TeamConfig {
    public static final SoleTeamConfig INSTANCE = new SoleTeamConfig();
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "individual");

    public static void init() {
        TeamConfigApi.API.register(ID, INSTANCE);
    }

    @Override
    public boolean test(Level level, UUID owner, UUID member) {
        return owner.equals(member);
    }
}
