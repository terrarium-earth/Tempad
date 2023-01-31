package me.codexadrian.tempad.fabric;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.PackType;

public class FabricTempadClient implements ClientModInitializer {
    public static ShaderInstance timedoorShader;
    public static ShaderInstance timedoorWhiteShader;

    public static final FabricBlurReloader INSTANCE = new FabricBlurReloader();
    public static final RenderType timedoorBlurRenderType = RenderType.create(
            "timedoorBlur",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> timedoorWhiteShader))
                    .setOutputState(new RenderStateShard.OutputStateShard("timedoor_blur", () -> {
                        if (TempadClient.getClientConfig().renderBlur()) {
                            RenderTarget renderTarget = INSTANCE.getRenderTarget();
                            if (renderTarget != null) {
                                renderTarget.bindWrite(false);
                            }
                        }
                    }, () -> {
                        if (TempadClient.getClientConfig().renderBlur()) {
                            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                        }
                    }))
                    .createCompositeState(false)
    );

    @Override
    public void onInitializeClient() {
        TempadClient.init();
        EntityRendererRegistry.register(FabricTempadRegistry.TIMEDOOR_ENTITY_ENTITY_TYPE, TimedoorRenderer::new);
        TempadClient.initItemProperties();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(INSTANCE);
    }
}
