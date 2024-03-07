package me.codexadrian.tempad.common.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import me.codexadrian.tempad.client.TempadClient;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import me.codexadrian.tempad.common.BlurReloader;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.utils.ClientUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = Tempad.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeTempadClient {
    public static ShaderInstance timedoorShader;
    public static final BlurReloader BLUR_RELOADER = new BlurReloader();

    private static final KeyMapping SHORTCUT_KEYBIND = new KeyMapping(
        "key.tempad.shortcut", // The translation key of the keybinding's name
        InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        InputConstants.UNKNOWN.getValue(), // The keycode of the key
        "category.tempad" // The translation key of the keybinding's category.
    );

    private static final KeyMapping FAVORITED_KEYBIND = new KeyMapping(
        "key.tempad.favorite", // The translation key of the keybinding's name
        InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        InputConstants.UNKNOWN.getValue(), // The keycode of the key
        "category.tempad" // The translation key of the keybinding's category.
    );

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        TempadClient.init();
        EntityRenderers.register(TempadRegistry.TIMEDOOR_ENTITY.get(), TimedoorRenderer::new);
        event.enqueueWork(TempadClient::initItemProperties);
        NeoForge.EVENT_BUS.addListener(ForgeTempadClient::onClientTick);
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(BLUR_RELOADER);
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(
                event.getResourceProvider(),
                new ResourceLocation("rendertype_timedoor"),
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP
        ), shaderInstance -> timedoorShader = shaderInstance);
    }

    @SubscribeEvent
    public static void registerKeyBinding(RegisterKeyMappingsEvent event) {
        event.register(SHORTCUT_KEYBIND);
        event.register(FAVORITED_KEYBIND);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            while (SHORTCUT_KEYBIND.consumeClick()) {
                ClientUtils.openTempadbyShortcut();
            }
            while (FAVORITED_KEYBIND.consumeClick()) {
                ClientUtils.openFavorited();
            }
        }
    }
}
