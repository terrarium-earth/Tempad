package me.codexadrian.tempad.common.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.codexadrian.tempad.client.BlurReloader;

import me.codexadrian.tempad.common.utils.ShaderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;"))
    private void tempad$renderLevel(CallbackInfo ci) {
        BlurReloader blurReloader = ShaderUtils.getBlurReloader();
        RenderTarget blurRenderTarget = blurReloader.getBlurTarget();
        if (blurRenderTarget == null) return;

        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        Matrix4f orthographicMatrix = new Matrix4f().setOrtho(0.0F, (float) mainTarget.width, (float) mainTarget.height, 0.0F, 0.1F, 1000.0F);

        float partialTicks = Minecraft.getInstance().getFrameTime();
        blurReloader.getFilterTimedoor().setOrthoMatrix(orthographicMatrix);

        blurReloader.getFilterTimedoor().process(partialTicks);

        mainTarget.bindWrite(false);
    }
}
