package me.codexadrian.tempad.common.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.codexadrian.tempad.common.BlurReloader;

import me.codexadrian.tempad.common.utils.ShaderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "resize", at = @At("TAIL"))
    public void tempad$resize(int i, int j, CallbackInfo ci) {
        BlurReloader blurReloader = ShaderUtils.getBlurReloader();
        PostChain timedoorBlur = blurReloader.getTimedoorBlur();
        if (timedoorBlur != null) {
            timedoorBlur.resize(i, j);
            blurReloader.getBlurSwapTarget().resize(i, j, Minecraft.ON_OSX);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V"))
    public void tempad$render(float partialTicks, long l, boolean bl, CallbackInfo ci) {
        BlurReloader blurReloader = ShaderUtils.getBlurReloader();

        RenderTarget blurTarget = blurReloader.getBlurTarget();
        if (blurTarget == null) return;

        blurReloader.getTimedoorBlur().process(partialTicks);
        blurTarget.clear(Minecraft.ON_OSX);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }
}
