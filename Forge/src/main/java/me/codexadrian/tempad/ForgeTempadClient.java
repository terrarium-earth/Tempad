package me.codexadrian.tempad;

import me.codexadrian.CommonClient;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;

public class ForgeTempadClient {
    public static ShaderInstance timedoorShader;
    public static ShaderInstance timedoorWhiteShader;
    public static final BlurReloader BLUR_RELOADER = new BlurReloader();

    public static void init() {
        CommonClient.init();
        EntityRenderers.register(ForgeTempad.TIMEDOOR.get(), TimedoorRenderer::new);
        MinecraftForge.EVENT_BUS.addListener(ForgeTempadClient::onAddReloadListener);
    }

    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(BLUR_RELOADER);
    }
}
