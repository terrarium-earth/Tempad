package me.codexadrian.tempad.common.utils.fabric;

import me.codexadrian.tempad.common.BlurReloader;
import me.codexadrian.tempad.common.fabric.FabricTempadClient;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

public class ShaderUtilsImpl {
    public static BlurReloader getBlurReloader() {
        return FabricTempadClient.INSTANCE;
    }

    public static RenderType getTimedoorShaderType() {
        return FabricTempadClient.RenderTypeAccessor.TIMEDOOR_BLUR_RENDER_TYPE;
    }

    public static void setTimedoorWhiteShader(ShaderInstance shader) {
        FabricTempadClient.timedoorWhiteShader = shader;
    }

    public static ShaderInstance getTimedoorWhiteShader() {
        return FabricTempadClient.timedoorWhiteShader;
    }

    public static void setTimeDoorShader(ShaderInstance shader) {
        FabricTempadClient.timedoorShader = shader;
    }

    public static ShaderInstance getTimedoorShader() {
        return FabricTempadClient.timedoorShader;
    }
}
