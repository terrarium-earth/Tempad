@file:Suppress("INACCESSIBLE_TYPE")

package earth.terrarium.tempad.client

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.entity.TimedoorRenderer
import earth.terrarium.tempad.client.screen.*
import earth.terrarium.tempad.common.registries.ModEntities
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.vanillaId
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.*
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.EntityRenderers
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterShadersEvent
import java.io.IOException

@EventBusSubscriber(modid = Tempad.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object TempadClient {
    var timedoorShader: ShaderInstance? = null
    val renderType: RenderType = CompositeState.builder()
        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
        .setCullState(NO_CULL)
        .setLightmapState(LIGHTMAP)
        .setLayeringState(NO_LAYERING)
        .setShaderState(ShaderStateShard { timedoorShader })
        .createCompositeState(false)
        .let {
            create(
                "timedoorBlur",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                it
            )
        }

    @SubscribeEvent @JvmStatic
    fun init(event: FMLClientSetupEvent) {
        EntityRenderers.register(ModEntities.TIMEDOOR_ENTITY, ::TimedoorRenderer)
        // NeoForge.EVENT_BUS.addListener<Event>(ForgeTempadClient::onClientTick)
    }

    @SubscribeEvent @JvmStatic
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenus.TELEPORT_MENU, ::TeleportScreen)
        event.register(ModMenus.NEW_LOCATION_MENU, ::NewLocationScreen)
        event.register(ModMenus.SETTINGS_MENU, ::SettingsScreen)
        event.register(ModMenus.TIMELINE_MENU, ::TimelineScreen)
        event.register(ModMenus.TPA_MENU, ::TpToScreen)
    }

    @SubscribeEvent @JvmStatic
    @Throws(IOException::class)
    fun registerShaders(event: RegisterShadersEvent) {
        event.registerShader(
            ShaderInstance(
                event.resourceProvider,
                "rendertype_timedoor".vanillaId,
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP
            )
        ) { shaderInstance: ShaderInstance ->
            timedoorShader = shaderInstance
        }
    }
}