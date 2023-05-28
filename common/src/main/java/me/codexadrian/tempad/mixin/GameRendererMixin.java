package me.codexadrian.tempad.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.platform.Services;
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
    public void resize(int i, int j, CallbackInfo ci) {
        BlurReloader blurReloader = Services.SHADERS.getBlurReloader();
        PostChain timedoorBlur = blurReloader.getTimedoorBlur();
        if (timedoorBlur != null) {
            timedoorBlur.resize(i, j);
            blurReloader.getBlurSwapTarget().resize(i, j, Minecraft.ON_OSX);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V"))
    public void render(float partialTicks, long l, boolean bl, CallbackInfo ci) {
        BlurReloader blurReloader = Services.SHADERS.getBlurReloader();

        RenderTarget blurTarget = blurReloader.getBlurTarget();
        if (blurTarget == null) return;

        blurReloader.getTimedoorBlur().process(partialTicks);
        blurTarget.clear(Minecraft.ON_OSX);
    }
}
