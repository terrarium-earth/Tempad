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
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "resize", at = @At("TAIL"))
    public void resize(int i, int j, CallbackInfo ci) {
        PostChain timedoorBlur = Services.SHADERS.getBlurReloader().getTimedoorBlur();
        if (timedoorBlur != null) timedoorBlur.resize(i, j);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V"))
    public void render(float partialTicks, long l, boolean bl, CallbackInfo ci) {
        RenderTarget finalRenderTarget = Services.SHADERS.getBlurReloader().getFinalTarget();
        if (finalRenderTarget == null) return;

        RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        mainRenderTarget.bindWrite(false);
        ShaderInstance finalBlurPass = Services.SHADERS.getBlurReloader().getFinalBlurPass();
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, mainRenderTarget.width, mainRenderTarget.height);
        if (bl) {
            GlStateManager._disableBlend();
        }

        finalBlurPass.setSampler("DiffuseSampler", mainRenderTarget.getColorTextureId());
        finalBlurPass.setSampler("TimedoorSampler", finalRenderTarget.getColorTextureId());
        Matrix4f matrix4f = Matrix4f.orthographic((float)mainRenderTarget.width, (float)(-mainRenderTarget.height), 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f);
        if (finalBlurPass.MODEL_VIEW_MATRIX != null) {
            finalBlurPass.MODEL_VIEW_MATRIX.set(Matrix4f.createTranslateMatrix(0.0F, 0.0F, -2000.0F));
        }

        if (finalBlurPass.PROJECTION_MATRIX != null) {
            finalBlurPass.PROJECTION_MATRIX.set(matrix4f);
        }

        finalBlurPass.apply();
        float f = (float)mainRenderTarget.width;
        float g = (float)mainRenderTarget.height;
        float h = (float)finalRenderTarget.viewWidth / (float)finalRenderTarget.width;
        float k = (float)finalRenderTarget.viewHeight / (float)finalRenderTarget.height;
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(0.0, g, 0.0).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(f, g, 0.0).uv(h, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(f, 0.0, 0.0).uv(h, k).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0.0F, k).color(255, 255, 255, 255).endVertex();
        BufferUploader.draw(bufferBuilder.end());
        finalBlurPass.clear();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
    }
}
