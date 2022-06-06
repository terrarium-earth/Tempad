package me.codexadrian.tempad.platform;

import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.ForgeTempadClient;
import me.codexadrian.tempad.TimedoorBlurRenderType;
import me.codexadrian.tempad.platform.services.IShaderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

public class ForgeShaderHelper implements IShaderHelper {
    @Override
    public ShaderInstance getTimedoorShader() {
        return ForgeTempadClient.timedoorShader;
    }

    @Override
    public void setTimeDoorShader(ShaderInstance shader) {
        ForgeTempadClient.timedoorShader = shader;
    }

    @Override
    public ShaderInstance getTimedoorWhiteShader() {
        return ForgeTempadClient.timedoorWhiteShader;
    }

    @Override
    public void setTimedoorWhiteShader(ShaderInstance shader) {
        ForgeTempadClient.timedoorWhiteShader = shader;
    }

    @Override
    public RenderType getTimedoorShaderType() {
        return TimedoorBlurRenderType.TIMEDOOR_BLUR_RENDER_TYPE;
    }

    @Override
    public BlurReloader getBlurReloader() {
        return ForgeTempadClient.BLUR_RELOADER;
    }
}
