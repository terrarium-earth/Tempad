package me.codexadrian.tempad.common.fabric;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.codexadrian.tempad.client.TempadClient;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import me.codexadrian.tempad.common.registry.TempadRegistry;
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

    @Override
    public void onInitializeClient() {
        TempadClient.init();
        EntityRendererRegistry.register(TempadRegistry.TIMEDOOR_ENTITY.get(), TimedoorRenderer::new);
        TempadClient.initItemProperties();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(INSTANCE);
    }

    public final static class RenderTypeAccessor extends RenderType {
        public static final RenderType TIMEDOOR_BLUR_RENDER_TYPE = RenderType.create(
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
                    RenderTarget renderTarget = INSTANCE.getBlurTarget();
                    if (renderTarget != null) {
                        renderTarget.bindWrite(false);
                    }
                }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)))
                .createCompositeState(false)
        );

        private RenderTypeAccessor(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }
    }
}
