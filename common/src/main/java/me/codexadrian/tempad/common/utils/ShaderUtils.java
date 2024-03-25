package me.codexadrian.tempad.common.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.client.BlurReloader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.apache.commons.lang3.NotImplementedException;

public class ShaderUtils {
    @ExpectPlatform
    public static ShaderInstance getTimedoorShader() {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static void setTimeDoorShader(ShaderInstance shader) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static RenderType getTimedoorShaderType() {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static BlurReloader getBlurReloader() {
        throw new NotImplementedException();
    }
}
