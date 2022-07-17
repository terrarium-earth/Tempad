package me.codexadrian.tempad.mixin.fabric;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
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
public class FabricGameRendererMixin {

    @Shadow
    @Final
    private Map<String, ShaderInstance> shaders;

    @Inject(method = "reloadShaders", at = @At("TAIL"))
    private void reloadShaders(ResourceManager resourceManager, CallbackInfo ci) {
        try {
            Services.SHADERS.setTimeDoorShader(new ShaderInstance(resourceManager, "rendertype_timedoor", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP));
            Services.SHADERS.setTimedoorWhiteShader(new ShaderInstance(resourceManager, "rendertype_timedoor_white", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP));
        } catch (Exception e) {
            ShaderInstance timedoorShader = Services.SHADERS.getTimedoorShader();
            ShaderInstance timedoorWhiteShader = Services.SHADERS.getTimedoorWhiteShader();


            if (timedoorShader != null) {
                timedoorShader.close();
                Services.SHADERS.setTimeDoorShader(null);
            } if (timedoorWhiteShader != null) {
                timedoorWhiteShader.close();
                Services.SHADERS.setTimedoorWhiteShader(null);
            }

            throw new RuntimeException("could not reload Tempad shaders", e);
        }
        ShaderInstance timedoorShader = Services.SHADERS.getTimedoorShader();
        ShaderInstance timedoorWhiteShader = Services.SHADERS.getTimedoorWhiteShader();

        this.shaders.put(timedoorShader.getName(), timedoorShader);
        this.shaders.put(timedoorWhiteShader.getName(), timedoorWhiteShader);
    }
}
