package me.codexadrian.tempad.forge;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeTempadClient {
    public static ShaderInstance timedoorShader;
    public static ShaderInstance timedoorWhiteShader;
    public static final BlurReloader BLUR_RELOADER = new BlurReloader();

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        TempadClient.init();
        EntityRenderers.register(ForgeTempad.TIMEDOOR.get(), TimedoorRenderer::new);
        event.enqueueWork(TempadClient::initItemProperties);
        FMLJavaModLoadingContext.get().getModEventBus().register(ForgeTempadClient.class);
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

    public static void registerBlurReloader() {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(BLUR_RELOADER);
    }
}
