package me.codexadrian.tempad.common.mixin.fabric;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import me.codexadrian.tempad.common.utils.ShaderUtils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
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
    private void reloadShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
        try {
            ShaderUtils.setTimeDoorShader(new ShaderInstance(resourceProvider, "rendertype_timedoor", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP));
        } catch (Exception e) {
            ShaderInstance timedoorShader = ShaderUtils.getTimedoorShader();

            if (timedoorShader != null) {
                timedoorShader.close();
                ShaderUtils.setTimeDoorShader(null);
            }

            throw new RuntimeException("could not reload Tempad shaders", e);
        }

        ShaderInstance timedoorShader = ShaderUtils.getTimedoorShader();

        this.shaders.put(timedoorShader.getName(), timedoorShader);
    }
}
