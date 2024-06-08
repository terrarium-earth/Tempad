package earth.terrarium.tempad.api.apps;

import earth.terrarium.tempad.api.ApiHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;

public interface TempadAppApi {
    TempadAppApi API = ApiHelper.load(TempadAppApi.class);

    void register(ResourceLocation id, TempadApp app);

    TempadApp getApp(ResourceLocation id);

    Map<ResourceLocation, TempadApp> getApps();

    ResourceLocation getHomePageId();

    void setHomePageId(ResourceLocation id);

    TempadApp getHomePage();
}
