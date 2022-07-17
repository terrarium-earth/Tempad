package me.codexadrian.tempad.forge;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "tempad", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeTempadClient {
    public static ShaderInstance timedoorShader;
    public static ShaderInstance timedoorWhiteShader;
    public static final BlurReloader BLUR_RELOADER = new BlurReloader();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        TempadClient.init();
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ForgeTempad.TIMEDOOR.get(), TimedoorRenderer::new);
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

    @SubscribeEvent
    public static void registerBlurReloader(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(BLUR_RELOADER);
    }
}
