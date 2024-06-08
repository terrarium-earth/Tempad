package earth.terrarium.tempad.client.apps;

import earth.terrarium.tempad.api.apps.TempadApp;
import earth.terrarium.tempad.api.apps.TempadAppApi;
import earth.terrarium.tempad.client.apps.impl.TeleportApp;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class TempadAppApiImpl implements TempadAppApi {
    private final Map<ResourceLocation, TempadApp> APPS = new HashMap<>();
    private ResourceLocation HOME_PAGE = TeleportApp.ID;

    @Override
    public void register(ResourceLocation id, TempadApp app) {
        APPS.put(id, app);
    }

    @Override
    public TempadApp getApp(ResourceLocation id) {
        return APPS.get(id);
    }

    @Override
    public Map<ResourceLocation, TempadApp> getApps() {
        return APPS;
    }

    @Override
    public ResourceLocation getHomePageId() {
        return HOME_PAGE;
    }

    @Override
    public void setHomePageId(ResourceLocation id) {
        HOME_PAGE = id;
    }

    @Override
    public TempadApp getHomePage() {
        return APPS.get(HOME_PAGE);
    }
}
