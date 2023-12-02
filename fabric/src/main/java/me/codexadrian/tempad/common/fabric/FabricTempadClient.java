package me.codexadrian.tempad.common.fabric;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.codexadrian.tempad.client.TempadClient;
import me.codexadrian.tempad.client.render.TimedoorRenderer;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import me.codexadrian.tempad.common.utils.ClientUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.PackType;
import org.lwjgl.glfw.GLFW;

public class FabricTempadClient implements ClientModInitializer {
    public static ShaderInstance timedoorShader;
    public static KeyMapping shortcutKey;
    public static KeyMapping favoriteKey;

    public static final FabricBlurReloader INSTANCE = new FabricBlurReloader();

    @Override
    public void onInitializeClient() {
        TempadClient.init();
        EntityRendererRegistry.register(TempadRegistry.TIMEDOOR_ENTITY.get(), TimedoorRenderer::new);
        TempadClient.initItemProperties();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(INSTANCE);

        shortcutKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.tempad.shortcut", // The translation key of the keybinding's name
            InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_V, // The keycode of the key
            "category.tempad.keybinds" // The translation key of the keybinding's category.
        ));

        favoriteKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.tempad.favorite", // The translation key of the keybinding's name
            InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_G, // The keycode of the key
            "category.tempad.keybinds" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (shortcutKey.consumeClick()) {
                ClientUtils.openTempadbyShortcut();
            }
            while (favoriteKey.consumeClick()) {
                ClientUtils.openFavorited();
            }
        });
    }

    public final static class RenderTypeAccessor extends RenderType {
        public static final RenderType TIMEDOOR_BLUR_RENDER_TYPE = RenderType.create(
            "timedoorBlur",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(new RenderStateShard.CullStateShard(false))
                .setShaderState(new RenderStateShard.ShaderStateShard(() -> timedoorShader))
                .setOutputState(new RenderStateShard.OutputStateShard("timedoor_blur", () -> {
                    RenderTarget renderTarget = INSTANCE.getBlurTarget();
                    if (renderTarget != null) {
                        renderTarget.bindWrite(false);
                    }
                }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)))
                .createCompositeState(false)
        );

        private RenderTypeAccessor(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }
    }
}
