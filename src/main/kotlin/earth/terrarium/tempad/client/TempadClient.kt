@file:Suppress("INACCESSIBLE_TYPE")

package earth.terrarium.tempad.client

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.ColorTargetState
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.datafixers.util.Either
import com.teamresourceful.resourcefullib.client.fluid.data.ClientFluidProperties
import com.teamresourceful.resourcefullib.client.fluid.registry.ResourcefulClientFluidRegistry
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.block.SpatialAnchorRenderer
import earth.terrarium.tempad.client.block.WorkstationRenderer
import earth.terrarium.tempad.client.compat.initCuriosCompat
import earth.terrarium.tempad.client.entity.TimedoorRenderer
import earth.terrarium.tempad.client.screen.ChronomarkScreen
import earth.terrarium.tempad.client.screen.MetronomeScreen
import earth.terrarium.tempad.client.screen.TimedoorMarkerScreen
import earth.terrarium.tempad.client.screen.WalletScreen
import earth.terrarium.tempad.client.screen.guide.KnowledgeScreen
import earth.terrarium.tempad.client.screen.tempad.KnowledgeAppScreen
import earth.terrarium.tempad.client.screen.tempad.NewLocationScreen
import earth.terrarium.tempad.client.screen.tempad.PortalSetupScreen
import earth.terrarium.tempad.client.screen.tempad.SettingsScreen
import earth.terrarium.tempad.client.screen.tempad.TeleportScreen
import earth.terrarium.tempad.client.screen.tempad.TimelineScreen
import earth.terrarium.tempad.client.tooltip.*
import earth.terrarium.tempad.client.model.BooleanComponentProperty
import earth.terrarium.tempad.client.model.ChrononChargeProperty
import earth.terrarium.tempad.client.model.WalletFullProperty
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
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.color.block.BlockTintSource
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent
import net.neoforged.neoforge.client.event.RenderTooltipEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

val TIMEDOOR_PIPELINE: RenderPipeline = RenderPipeline.builder()
    .withLocation("tempad:timedoor")
    .withVertexShader("minecraft:core/rendertype_timedoor")
    .withFragmentShader("minecraft:core/rendertype_timedoor")
    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS)
    .withCull(false)
    .withColorTargetState(ColorTargetState(BlendFunction.ADDITIVE))
    .build()

val TIMEDOOR_TEX_PIPELINE: RenderPipeline = RenderPipeline.builder()
    .withLocation("tempad:timedoor_tex")
    .withVertexShader("minecraft:core/position_tex_color")
    .withFragmentShader("minecraft:core/position_tex_color")
    .withSampler("Sampler0")
    .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
    .withCull(false)
    .withColorTargetState(ColorTargetState(BlendFunction.ADDITIVE))
    .build()

@EventBusSubscriber(modid = Tempad.MOD_ID, value = [Dist.CLIENT])
object TempadClient {
    fun renderType(textureId: Identifier?): RenderType {
        val builder = RenderSetup.builder(
            if (textureId != null) TIMEDOOR_TEX_PIPELINE else TIMEDOOR_PIPELINE
        )
        if (textureId != null) {
            builder.withTexture("Sampler0", textureId)
        }
        builder.setOutputTarget(OutputTarget.MAIN_TARGET)
        builder.bufferSize(256)
        return RenderType.create("timedoor", builder.createRenderSetup())
    }

    val blockTintSource: BlockTintSource = object : BlockTintSource {
        override fun color(state: BlockState): Int = Tempad.ORANGE.value

        override fun colorInWorld(state: BlockState, tintGetter: BlockAndTintGetter, pos: BlockPos): Int {
            return safeLet(tintGetter as? net.minecraft.world.level.BlockGetter, pos) { level, blockPos ->
                val blockEntity = level.getBlockEntity(blockPos)
                blockEntity?.color?.value
            } ?: Tempad.ORANGE.value
        }
    }

    val clientFluidRegistry = ResourcefulClientFluidRegistry(Tempad.MOD_ID)

    val chrononRenderer = ClientFluidProperties.builder().apply {
        still("block/water_still".vanillaId)
        flowing("block/water_flow".vanillaId)
        overlay("block/water_overlay".vanillaId)
        screenOverlay("textures/misc/underwater.png".vanillaId)
        tintColor(Tempad.ORANGE.value)
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
        BlockEntityRenderers.register(ModBlocks.timedoorMarkerBE) { SpatialAnchorRenderer(it.blockModelResolver()) }
        BlockEntityRenderers.register(ModBlocks.chronomarkBE) { SpatialAnchorRenderer(it.blockModelResolver()) }
        BlockEntityRenderers.register(ModBlocks.workstationBE) { WorkstationRenderer(it.itemModelResolver()) }

        /*
        if (ModList.get().isLoaded("ars_nouveau")) {
            ArsCompat.init()
        }
         */

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
        event.register(ModMenus.guide, ::KnowledgeAppScreen)
        event.register(ModMenus.metronome, ::MetronomeScreen)
        event.register(ModMenus.wallet, ::WalletScreen)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerPipelines(event: RegisterRenderPipelinesEvent) {
        event.registerPipeline(TIMEDOOR_PIPELINE)
        event.registerPipeline(TIMEDOOR_TEX_PIPELINE)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerTooltip(event: RegisterClientTooltipComponentFactoriesEvent) {
        event.register(ChrononData::class.java, ::ChrononTooltip)
        event.register(InstalledUpgradesComponent::class.java, ::UpgradesTooltip)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerBlockColors(event: RegisterColorHandlersEvent.BlockTintSources) {
        event.register(listOf(blockTintSource), ModBlocks.timedoorMarker)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerItemColors(event: RegisterColorHandlersEvent.ItemTintSources) {
        event.register(Identifier.fromNamespaceAndPath(Tempad.MOD_ID, "tempad_color"), TempadColorItemTintSource.CODEC)
    }

    fun appendTooltip(event: RenderTooltipEvent.GatherComponents) {
        val stack = event.itemStack
        if (stack.item === ModItems.tempad && stack.installedUpgrades.upgrades.isNotEmpty()) {
            event.tooltipElements.add(2, Either.right(stack.installedUpgrades))
        }
    }

    fun onTooltipAdded(event: ItemTooltipEvent) {
        (event.itemStack.portalTarget as? TooltipProvider)?.addToTooltip(event.context, event.toolTip::add, event.flags, event.itemStack)
    }

    fun openTimedoorMarker(packet: OpenTimedoorMarker) {
        Minecraft.getInstance().setScreen(TimedoorMarkerScreen(packet.blockPos, packet.name, packet.color, packet.canAccess, packet.locked))
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

    @SubscribeEvent
    @JvmStatic
    fun registerConditionalItemModelProperties(event: RegisterConditionalItemModelPropertyEvent) {
        event.register("tempad:boolean_component".tempadId, BooleanComponentProperty.MAP_CODEC)
        event.register("tempad:wallet_full".tempadId, WalletFullProperty.MAP_CODEC)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerRangeSelectItemModelProperties(event: RegisterRangeSelectItemModelPropertyEvent) {
        event.register("tempad:chronon_charge".tempadId, ChrononChargeProperty.MAP_CODEC)
    }
}

val clientLevel: Level? get() = Minecraft.getInstance()?.level
