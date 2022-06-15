package me.codexadrian.tempad.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.client.render.TimedoorBlurRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("TAIL"))
    public void renderBlur(PoseStack poseStack, float deltaTime, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        if(TempadClient.getClientConfig().renderBlur() && !Minecraft.useShaderTransparency()) {
            TimedoorBlurRenderer.renderBlur(deltaTime, poseStack, camera);
        }
    }
}
