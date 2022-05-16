package me.codexadrian.tempad;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

public class ForgeTempadClient {
    public static ShaderInstance timedoorShader;
    public static ShaderInstance timedoorWhiteShader;
    public static final BlurReloader BLUR_RELOADER = new BlurReloader();

    public static void init() {
        TempadClient.init();
        EntityRenderers.register(ForgeTempad.TIMEDOOR.get(), TimedoorRenderer::new);
        FMLJavaModLoadingContext.get().getModEventBus().register(ForgeTempadClient.class);
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(BLUR_RELOADER);
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(
                event.getResourceManager(),
                new ResourceLocation("rendertype_timedoor"),
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP
        ), shaderInstance -> timedoorShader = shaderInstance);

        event.registerShader(new ShaderInstance(
                event.getResourceManager(),
                new ResourceLocation("rendertype_timedoor_white"),
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP
        ), shaderInstance -> timedoorWhiteShader = shaderInstance);
    }
}
