package earth.terrarium.tempad.api.teams;

import earth.terrarium.tempad.api.ApiHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public interface TeamConfigApi {
    TeamConfigApi API = ApiHelper.load(TeamConfigApi.class);

    void register(ResourceLocation id, TeamConfig config);

    TeamConfig getConfig(ResourceLocation id);

    Collection<ResourceLocation> getIds();
}
