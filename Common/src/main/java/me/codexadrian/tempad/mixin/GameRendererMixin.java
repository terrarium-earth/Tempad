package me.codexadrian.tempad.mixin;

import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "resize", at = @At("TAIL"))
    public void resize(int i, int j, CallbackInfo ci) {
        PostChain timedoorBlur = Services.SHADERS.getBlurReloader().getTimedoorBlur();
        if (timedoorBlur != null) timedoorBlur.resize(i, j);
    }
}
