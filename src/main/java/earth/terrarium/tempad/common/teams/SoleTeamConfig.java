package earth.terrarium.tempad.common.teams;

import earth.terrarium.tempad.api.teams.TeamConfig;
import earth.terrarium.tempad.api.teams.TeamConfigApi;
import earth.terrarium.tempad.common.Tempad;
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
