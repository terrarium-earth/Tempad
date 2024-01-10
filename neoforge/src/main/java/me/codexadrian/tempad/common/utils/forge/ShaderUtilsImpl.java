package me.codexadrian.tempad.common.utils.forge;

import me.codexadrian.tempad.common.BlurReloader;
import me.codexadrian.tempad.common.neoforge.ForgeTempadClient;
import me.codexadrian.tempad.common.neoforge.TimedoorBlurRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

public class ShaderUtilsImpl {
    public static ShaderInstance getTimedoorShader() {
        return ForgeTempadClient.timedoorShader;
    }

    public static void setTimeDoorShader(ShaderInstance shader) {
        ForgeTempadClient.timedoorShader = shader;
    }

    public static RenderType getTimedoorShaderType() {
        return TimedoorBlurRenderType.TIMEDOOR_BLUR_RENDER_TYPE;
    }

    public static BlurReloader getBlurReloader() {
        return ForgeTempadClient.BLUR_RELOADER;
    }
}
