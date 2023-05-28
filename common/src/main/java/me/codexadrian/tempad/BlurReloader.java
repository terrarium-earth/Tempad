package me.codexadrian.tempad;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;

import java.io.IOException;

public class BlurReloader implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    private PostChain timedoorBlur;
    private PostPass filterTimedoor;
    private PostPass swapBlurTargets;
    private RenderTarget blurTarget;
    private RenderTarget blurSwapTarget;

    public BlurReloader() {
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (!TempadClient.getClientConfig().renderBlur()) {
            return;
        }

        var minecraft = Minecraft.getInstance();

        if (timedoorBlur != null) timedoorBlur.close();
        if (blurSwapTarget != null) blurSwapTarget.destroyBuffers();
        if (filterTimedoor != null) filterTimedoor.close();
        if (swapBlurTargets != null) swapBlurTargets.close();

        ResourceLocation resourceLocation = new ResourceLocation("shaders/post/timedoorblur.json");
        try {
            timedoorBlur = new PostChain(minecraft.getTextureManager(), resourceManager, minecraft.getMainRenderTarget(), resourceLocation);
            timedoorBlur.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            blurTarget = timedoorBlur.getTempTarget("blur_target");
            blurSwapTarget = new TextureTarget(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), true, Minecraft.ON_OSX);

            filterTimedoor = new PostPass(resourceManager, "filter_timedoor_rendering", blurTarget, blurSwapTarget);
            swapBlurTargets = new PostPass(resourceManager, "blit", blurSwapTarget, blurTarget);

            RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
            filterTimedoor.addAuxAsset("DiffuseDepthSampler", blurTarget::getDepthTextureId, blurTarget.width, blurTarget.height);
            filterTimedoor.addAuxAsset("WorldDepthSampler", mainRenderTarget::getDepthTextureId, mainRenderTarget.width, mainRenderTarget.height);
        } catch (JsonSyntaxException | IOException exception) {
            LOGGER.error("Failed to load Tempad shaders", exception);

            timedoorBlur = null;
            blurTarget = null;

            filterTimedoor = null;
        }
    }

    public PostChain getTimedoorBlur() {
        return timedoorBlur;
    }

    public RenderTarget getBlurTarget() {
        return blurTarget;
    }

    public RenderTarget getBlurSwapTarget() {
        return blurSwapTarget;
    }

    public PostPass getFilterTimedoor() {
        return filterTimedoor;
    }

    public PostPass getSwapBlurTargets() {
        return swapBlurTargets;
    }
}
