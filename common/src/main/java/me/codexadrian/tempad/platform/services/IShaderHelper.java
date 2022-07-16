package me.codexadrian.tempad.platform.services;

import me.codexadrian.tempad.BlurReloader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public interface IShaderHelper {

    ShaderInstance getTimedoorShader();

    void setTimeDoorShader(ShaderInstance shader);

    ShaderInstance getTimedoorWhiteShader();

    void setTimedoorWhiteShader(ShaderInstance shader);

    RenderType getTimedoorShaderType();

    BlurReloader getBlurReloader();
}
