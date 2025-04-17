@file:Suppress("INACCESSIBLE_TYPE")

package earth.terrarium.tempad.client

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.datafixers.util.Either
import com.teamresourceful.resourcefullib.client.fluid.data.ClientFluidProperties
import com.teamresourceful.resourcefullib.client.fluid.registry.ResourcefulClientFluidRegistry
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.client.block.SpatialAnchorRenderer
import earth.terrarium.tempad.client.block.WorkstationRenderer
import earth.terrarium.tempad.client.compat.initCuriosCompat
import earth.terrarium.tempad.client.entity.TimedoorRenderer
import earth.terrarium.tempad.client.screen.ChronomarkScreen
import earth.terrarium.tempad.client.screen.MetronomeScreen
import earth.terrarium.tempad.client.screen.TimedoorMarkerScreen
import earth.terrarium.tempad.client.screen.WalletScreen
import earth.terrarium.tempad.client.screen.guide.KnowledgeScreen
import earth.terrarium.tempad.client.screen.tempad.NewLocationScreen
import earth.terrarium.tempad.client.screen.tempad.PortalSetupScreen
import earth.terrarium.tempad.client.screen.tempad.SettingsScreen
import earth.terrarium.tempad.client.screen.tempad.TeleportScreen
import earth.terrarium.tempad.client.screen.tempad.TimelineScreen
import earth.terrarium.tempad.client.tooltip.*
import earth.terrarium.tempad.common.compat.ArsCompat
import earth.terrarium.tempad.common.config.ClientConfig
import earth.terrarium.tempad.common.data.InstalledUpgradesComponent
import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import earth.terrarium.tempad.common.network.s2c.OpenChronomark
import earth.terrarium.tempad.common.network.s2c.OpenTimedoorMarker
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.vanillaId
import earth.terrarium.tempad.tempadId
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockColor
import net.minecraft.client.color.item.ItemColor
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.*
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterShadersEvent
import net.neoforged.neoforge.client.event.RenderTooltipEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
import java.io.IOException

@EventBusSubscriber(modid = Tempad.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object TempadClient {
    var timedoorShader: ShaderInstance? = null
    fun renderType(textureId: ResourceLocation?): RenderType = CompositeState.builder()
        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
        .setCullState(NO_CULL)
        .setLayeringState(NO_LAYERING)
        .setShaderState(ShaderStateShard {
            textureId?.let { GameRenderer.getPositionTexColorShader() } ?: timedoorShader
        })
        .apply {
            if (textureId != null) {
                setTextureState(RenderStateShard.TextureStateShard(textureId, false, true))
            }
        }
        .setOutputState(PARTICLES_TARGET)
        .createCompositeState(true)
        .let {
            create(
                "timedoor",
                textureId?.let { DefaultVertexFormat.POSITION_TEX_COLOR } ?: DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                it
            )
        }

    val enabledProperty = BooleanItemPropertyFunction { stack, level, entity, seed -> stack.enabled }

    val screeningEnabled = BooleanItemPropertyFunction { stack, level, entity, seed -> stack.accessId != null }

    val twisterAttachedProperty = BooleanItemPropertyFunction { stack, level, entity, seed -> stack.twisterEquipped }

    val inUseProperty = BooleanItemPropertyFunction { stack, level, entity, seed ->
        if (entity !is Player) return@BooleanItemPropertyFunction false
        val menu = entity.containerMenu
        if (menu !is AbstractTempadMenu<*>) return@BooleanItemPropertyFunction false
        return@BooleanItemPropertyFunction menu.ctx.stack === stack
    }

    val charge4Property = ClampedItemPropertyFunction { stack, level, entity, seed ->
        val tank = stack.chronons ?: return@ClampedItemPropertyFunction 0f
        return@ClampedItemPropertyFunction step(tank.power.toFloat() / tank.maxPower, 0.25f)
    }

    val writtenProperty = BooleanItemPropertyFunction { stack, level, entity, seed -> stack.portalTarget != null }

    val hasCardsProperty = BooleanItemPropertyFunction { stack, level, entity, seed ->
        if(stack.`is`(ModItems.cardWallet)) {
            for (stack in stack.walletContents.nonEmptyItems()) {
                if (!stack.isEmpty) return@BooleanItemPropertyFunction true
            }
        }
        return@BooleanItemPropertyFunction false
    }

    val clientFluidRegistry = ResourcefulClientFluidRegistry(Tempad.MOD_ID)

    val chrononRenderer = ClientFluidProperties.builder().apply {
        still("block/water_still".vanillaId)
        flowing("block/water_flow".vanillaId)
        overlay("block/water_overlay".vanillaId)
        screenOverlay("textures/misc/underwater.png".vanillaId)
        tintColor(Tempad.ORANGE.value)
    }

    val blockColor: BlockColor = BlockColor { _, maybeLevel, maybePos, _ ->
        safeLet(maybeLevel as? BlockGetter, maybePos) { level, pos ->
            val blockEntity = level.getBlockEntity(pos)
            (blockEntity?.color)?.value?.let { return@BlockColor it }
        } ?: Tempad.ORANGE.value
    }

    val itemColor: ItemColor = ItemColor { stack, _ ->
        stack.color?.value ?: Tempad.ORANGE.value
    }

    fun step(value: Float, step: Float): Float {
        if (value == 0f) return 0f
        if (value == 1f) return 1f
        var current = step
        while (value > current && current + step <= 1f) {
            current += step
        }
        return current
    }

    init {
        clientFluidRegistry.register("chronon", chrononRenderer)
        NeoForge.EVENT_BUS.addListener(::appendTooltip)
        NeoForge.EVENT_BUS.addListener(::onTooltipAdded)
        Tempad.CONFIGURATOR.register(ClientConfig::class.java)
    }

    @SubscribeEvent
    @JvmStatic
    fun init(event: FMLClientSetupEvent) {
        EntityRenderers.register(ModEntities.timedoor, ::TimedoorRenderer)
        ItemProperties.register(ModItems.tempad, "in_use".tempadId, inUseProperty)
        ItemProperties.register(ModItems.tempad, "attached".tempadId, twisterAttachedProperty)
        ItemProperties.register(ModItems.tempad, "charge".tempadId, charge4Property)
        ItemProperties.register(ModItems.chrononCell, "charge".tempadId, charge4Property)
        ItemProperties.register(ModItems.chrononBattery, "charge".tempadId, charge4Property)
        ItemProperties.register(ModItems.chronometer, "charge".tempadId, charge4Property)
        ItemProperties.register(ModItems.chrononGenerator, "charge".tempadId, charge4Property)
        ItemProperties.register(ModItems.locationBroadcaster, "enabled".tempadId, enabledProperty)
        ItemProperties.register(ModItems.screeningDevice, "enabled".tempadId, screeningEnabled)
        ItemProperties.register(ModItems.locationCard, "written".tempadId, writtenProperty)
        ItemProperties.register(ModItems.timedoorProjector, "has_card".tempadId, writtenProperty)
        ItemProperties.register(ModItems.cardWallet, "full".tempadId, hasCardsProperty)
        BlockEntityRenderers.register(ModBlocks.timedoorMarkerBE) { SpatialAnchorRenderer(it.blockRenderDispatcher) }
        BlockEntityRenderers.register(ModBlocks.chronomarkBE) { SpatialAnchorRenderer(it.blockRenderDispatcher) }
        BlockEntityRenderers.register(ModBlocks.workstationBE) { WorkstationRenderer(it.itemRenderer) }

        if (ModList.get().isLoaded("ars_nouveau")) {
            ArsCompat.init()
        }

        if (ModList.get().isLoaded("curios")) {
            initCuriosCompat()
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenus.teleport, ::TeleportScreen)
        event.register(ModMenus.newLocation, ::NewLocationScreen)
        event.register(ModMenus.settings, ::SettingsScreen)
        event.register(ModMenus.timeline, ::TimelineScreen)
        event.register(ModMenus.portalSetup, ::PortalSetupScreen)
        event.register(ModMenus.metronome, ::MetronomeScreen)
        event.register(ModMenus.wallet, ::WalletScreen)
    }

    @SubscribeEvent
    @JvmStatic
    @Throws(IOException::class)
    fun registerShaders(event: RegisterShadersEvent) {
        event.registerShader(
            ShaderInstance(
                event.resourceProvider,
                "rendertype_timedoor".vanillaId,
                DefaultVertexFormat.POSITION_COLOR
            )
        ) { shaderInstance: ShaderInstance ->
            timedoorShader = shaderInstance
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun registerTooltip(event: RegisterClientTooltipComponentFactoriesEvent) {
        event.register(ChrononData::class.java, ::ChrononTooltip)
        event.register(InstalledUpgradesComponent::class.java, ::UpgradesTooltip)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerBlockColors(event: RegisterColorHandlersEvent.Block) {
        event.register(blockColor, ModBlocks.timedoorMarker)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(itemColor, ModItems.timedoorMarker)
        event.register(itemColor, ModItems.chronomark)
    }

    fun appendTooltip(event: RenderTooltipEvent.GatherComponents) {
        val stack = event.itemStack
        if (stack.item === ModItems.tempad && stack.installedUpgrades.upgrades.isNotEmpty()) {
            event.tooltipElements.add(2, Either.right(stack.installedUpgrades))
        }
    }

    fun onTooltipAdded(event: ItemTooltipEvent) {
        (event.itemStack.portalTarget as? TooltipProvider)?.addToTooltip(event.context, event.toolTip::add, event.flags)
    }

    fun openTimedoorMarker(packet: OpenTimedoorMarker) {
        Minecraft.getInstance().setScreen(TimedoorMarkerScreen(packet.blockPos, packet.name, packet.color, packet.accessOptions, packet.access, packet.locked))
    }

    fun openChronomark(packet: OpenChronomark) {
        Minecraft.getInstance().setScreen(ChronomarkScreen(packet.blockPos, packet.name, packet.color, packet.accessOptions, packet.access, packet.locked, packet.yOffset, packet.angle))
    }

    fun openGuide() {
        Minecraft.getInstance().setScreen(KnowledgeScreen())
    }

    @SubscribeEvent
    @JvmStatic
    fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
        event.registerItem(RudimentaryTempadClient, ModItems.timedoorProjector)
    }
}

val clientLevel: Level? get() = Minecraft.getInstance().level