package me.codexadrian.tempad;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.IOException;

public class BlurReloader implements ResourceManagerReloadListener {
    private PostChain timedoorBlur;
    private ShaderInstance finalBlurPass;
    private RenderTarget blurTarget;
    private RenderTarget finalTarget;

    public BlurReloader() {}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (!TempadClient.getClientConfig().renderBlur()) {
            return;
        }

        var minecraft = Minecraft.getInstance();

        if (timedoorBlur != null) {
            timedoorBlur.close();
        }

        ResourceLocation resourceLocation = new ResourceLocation("shaders/post/timedoorblur.json");
        try {
            timedoorBlur = new PostChain(minecraft.getTextureManager(), resourceManager, minecraft.getMainRenderTarget(), resourceLocation);
            timedoorBlur.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            blurTarget = timedoorBlur.getTempTarget("blur_target");
            finalTarget = timedoorBlur.getTempTarget("final");

            finalBlurPass = new ShaderInstance(resourceManager, "program/final_blur_pass", DefaultVertexFormat.POSITION);
        } catch (JsonSyntaxException | IOException var4) {
            timedoorBlur = null;
            blurTarget = null;
            finalTarget = null;
        }
    }

    public PostChain getTimedoorBlur() {
        return timedoorBlur;
    }

    public RenderTarget getBlurTarget() {
        return blurTarget;
    }

    public RenderTarget getFinalTarget() {
        return finalTarget;
    }

    public ShaderInstance getFinalBlurPass() {
        return finalBlurPass;
    }
}
