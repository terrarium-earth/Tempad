package me.codexadrian.tempad.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;"))
    private void renderTimedoors(CallbackInfo ci) {
        BlurReloader blurReloader = Services.SHADERS.getBlurReloader();
        RenderTarget blurRenderTarget = blurReloader.getBlurTarget();
        if (blurRenderTarget == null) return;

        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();

        RenderTarget blurSwapTarget = blurReloader.getBlurSwapTarget();
        blurSwapTarget.bindWrite(false);

        EffectInstance timedoorFilter = blurReloader.getFilterTimedoor();
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, mainTarget.width, mainTarget.height);

        timedoorFilter.setSampler("DiffuseSampler", blurRenderTarget::getColorTextureId);
        timedoorFilter.setSampler("DiffuseDepthSampler", blurRenderTarget::getDepthTextureId);
        timedoorFilter.setSampler("WorldDepthSampler", mainTarget::getDepthTextureId);

        Matrix4f matrix4f = Matrix4f.orthographic((float) mainTarget.width, (float) (-mainTarget.height), 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f);
        timedoorFilter.safeGetUniform("ProjMat").set(matrix4f);
        timedoorFilter.safeGetUniform("InSize").set((float) mainTarget.width, (float) mainTarget.height);
        timedoorFilter.safeGetUniform("OutSize").set((float) blurSwapTarget.width, (float) blurSwapTarget.height);

        timedoorFilter.apply();
        float f = (float) mainTarget.width;
        float g = (float) mainTarget.height;
        float h = (float) blurSwapTarget.viewWidth / (float) blurSwapTarget.width;
        float k = (float) blurSwapTarget.viewHeight / (float) blurSwapTarget.height;
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(0.0, g, 0.0).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(f, g, 0.0).uv(h, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(f, 0.0, 0.0).uv(h, k).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0.0F, k).color(255, 255, 255, 255).endVertex();
        BufferUploader.draw(bufferBuilder.end());
        timedoorFilter.clear();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);

        blurRenderTarget.clear(Minecraft.ON_OSX);
        blurRenderTarget.bindWrite(false);
        blurSwapTarget.blitToScreen(blurRenderTarget.width, blurRenderTarget.height);

        mainTarget.bindWrite(false);
    }
}
