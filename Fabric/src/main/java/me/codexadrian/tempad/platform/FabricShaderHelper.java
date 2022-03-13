package me.codexadrian.tempad.platform;

import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.FabricTempadClient;
import me.codexadrian.tempad.platform.services.IShaderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

public class FabricShaderHelper implements IShaderHelper {
    @Override
    public ShaderInstance getTimedoorShader() {
        return FabricTempadClient.timedoorShader;
    }

    @Override
    public void setTimeDoorShader(ShaderInstance shader) {
        FabricTempadClient.timedoorShader = shader;
    }

    @Override
    public ShaderInstance getTimedoorWhiteShader() {
        return FabricTempadClient.timedoorWhiteShader;
    }

    @Override
    public void setTimedoorWhiteShader(ShaderInstance shader) {
        FabricTempadClient.timedoorWhiteShader = shader;
    }

    @Override
    public RenderType getTimedoorShaderType() {
        return FabricTempadClient.timedoorBlurRenderType;
    }

    @Override
    public BlurReloader getBlurReloader() {
        return FabricTempadClient.INSTANCE;
    }
}
