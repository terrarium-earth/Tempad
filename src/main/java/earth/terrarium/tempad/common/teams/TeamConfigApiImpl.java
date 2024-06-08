package earth.terrarium.tempad.common.teams;

import earth.terrarium.tempad.api.teams.TeamConfig;
import earth.terrarium.tempad.api.teams.TeamConfigApi;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TeamConfigApiImpl implements TeamConfigApi {
    private final Map<ResourceLocation, TeamConfig> teams = new HashMap<>();

    @Override
    public void register(ResourceLocation id, TeamConfig config) {
        teams.put(id, config);
    }

    @Override
    public TeamConfig getConfig(ResourceLocation id) {
        return teams.get(id);
    }

    @Override
    public Collection<ResourceLocation> getIds() {
        return teams.keySet();
    }
}
