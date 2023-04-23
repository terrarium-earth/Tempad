package me.codexadrian.tempad.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;

public class TimedoorBlurRenderer {
    public static void renderBlur(float partialTicks) {
        RenderTarget blurRenderTarget = Services.SHADERS.getBlurReloader().getRenderTarget();
        if (blurRenderTarget == null) return;

        Services.SHADERS.getBlurReloader().getTimedoorBlur().process(partialTicks);
        blurRenderTarget.clear(Minecraft.ON_OSX);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }
}
