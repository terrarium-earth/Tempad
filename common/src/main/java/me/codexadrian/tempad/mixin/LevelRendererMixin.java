package me.codexadrian.tempad.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.codexadrian.tempad.client.render.TimedoorBlurRenderer;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;shouldShowEntityOutlines()Z", ordinal = 0))
    private void copyDepth(CallbackInfo ci) {
        RenderTarget renderTarget = Services.SHADERS.getBlurReloader().getRenderTarget();
        if (renderTarget == null) return;

        renderTarget.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }
}
