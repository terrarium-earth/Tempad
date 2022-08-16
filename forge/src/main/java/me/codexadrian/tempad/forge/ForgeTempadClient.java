package me.codexadrian.tempad.forge;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import me.codexadrian.tempad.BlurReloader;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.client.gui.blockmanager.TempadBlockScreen;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import me.codexadrian.tempad.registry.TempadEntities;
import me.codexadrian.tempad.registry.TempadMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

public class ForgeTempadClient {
    public static ShaderInstance timedoorShader;
    public static ShaderInstance timedoorWhiteShader;
    public static final BlurReloader BLUR_RELOADER = new BlurReloader();

    public static void init() {
        TempadClient.init();
        EntityRenderers.register(TempadEntities.TIMEDOOR_ENTITY_TYPE.get(), TimedoorRenderer::new);
        EntityRenderers.register(TempadEntities.BLOCK_TIMEDOOR_ENTITY_TYPE.get(), TimedoorRenderer::new);
        FMLJavaModLoadingContext.get().getModEventBus().register(ForgeTempadClient.class);
        MenuScreens.register(TempadMenus.TEMPAD_BLOCK_MENU.get(), TempadBlockScreen::new);
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
