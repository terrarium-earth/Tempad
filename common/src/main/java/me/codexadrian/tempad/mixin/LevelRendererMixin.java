package me.codexadrian.tempad.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;"))
    private void renderBlur(CallbackInfo ci) {
        RenderTarget blurRenderTarget = Services.SHADERS.getBlurReloader().getBlurTarget();
        if (blurRenderTarget == null) return;

        Services.SHADERS.getBlurReloader().getTimedoorBlur().process(Minecraft.getInstance().getFrameTime());
        blurRenderTarget.clear(Minecraft.ON_OSX);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }
}
