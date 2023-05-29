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
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.PostPass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.IntSupplier;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;"))
    private void renderTimedoors(CallbackInfo ci) {
        BlurReloader blurReloader = Services.SHADERS.getBlurReloader();
        RenderTarget blurRenderTarget = blurReloader.getBlurTarget();
        if (blurRenderTarget == null) return;

        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        Matrix4f orthographicMatrix = Matrix4f.orthographic(0.0F, (float) mainTarget.width, (float) mainTarget.height, 0.0F, 0.1F, 1000.0F);

        float partialTicks = Minecraft.getInstance().getFrameTime();
        blurReloader.getFilterTimedoor().setOrthoMatrix(orthographicMatrix);
        blurReloader.getSwapBlurTargets().setOrthoMatrix(orthographicMatrix);

        blurReloader.getFilterTimedoor().process(partialTicks);
        blurReloader.getSwapBlurTargets().process(partialTicks);

        mainTarget.bindWrite(false);
    }
}
