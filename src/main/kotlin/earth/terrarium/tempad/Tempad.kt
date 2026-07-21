package earth.terrarium.tempad

import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullibkt.common.id
import earth.terrarium.tempad.api.capabilities.TempadCapabilities
import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.capabilities.player_access.DefaultLocationAccess
import earth.terrarium.tempad.api.capabilities.player_access.PlayerLocationAccess
import earth.terrarium.tempad.api.capabilities.upgrades.impl.ItemUpgradeHandler
import earth.terrarium.tempad.api.capabilities.chronons.TempadChrononHandler
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.client.clientLevel
import earth.terrarium.tempad.common.block.MetronomeBe
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import earth.terrarium.tempad.common.block.WorkstationBE
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.data.TravelHistoryAttachment
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.items.ScreeningDeviceLocationAccess
import earth.terrarium.tempad.common.items.items
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.access
import earth.terrarium.tempad.common.utils.register
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlag
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.NormalCraftingRecipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RecipesReceivedEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent
import net.neoforged.neoforge.common.world.chunk.TicketController
import net.neoforged.neoforge.event.AddPackFindersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import net.neoforged.neoforge.transfer.energy.InfiniteEnergyHandler
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val String.tempadId: Identifier
    get() = Identifier.fromNamespaceAndPath(Tempad.MOD_ID, this)

@Mod(Tempad.MOD_ID)
class Tempad(bus: IEventBus) {
    companion object {
        const val MOD_ID = "tempad"
        val DARK_ORANGE: Color = Color(0x91, 0x45, 0x0d, 255)
        val ORANGE: Color = Color(0xFF, 0x6a, 0, 255)
        val HIGHLIGHTED_ORANGE: Color = Color(0xFF, 0xcc, 0x54, 255)
        val GREEN = Color(0xFF65fb01.toInt())

        val CONFIGURATOR = Configurator(MOD_ID)

        val server: MinecraftServer?
            get() = ServerLifecycleHooks.getCurrentServer();

        val level: Level?
            get() = server?.overworld() ?: clientLevel;

        val logger: Logger = LogManager.getLogger(MOD_ID)

        val ticketController = TicketController("timedoor".tempadId, null)

        val clientRecipes = mutableMapOf<Identifier, NormalCraftingRecipe>()
    }

    init {
        CONFIGURATOR.register(CommonConfig::class.java)

        ModApps.init()
        ModSizing.init()
        ModAttachments.registry.init()
        ModComponents.registry.init()
        ModItemAccess.init()
        ModEntities.entities.init()
        ModEntities.serializers.init()
        ModBlocks.blocks.init()
        ModBlocks.blockEntities.init()
        ModItems.registry.init()
        ModItems.creativeTabs.init()
        ModMacros.init()
        ModMenus.registry.init()
        ModNetworking.init()
        ModRecipes.init()
        ModSounds.registry.init()
        ModLocations.init()
        CommonConfigCache.init()

      /*  if (ModList.get().isLoaded("cadmus")) {
            CadmusCompat.init()
        }
*/
        bus.addListener { event: RegisterCapabilitiesEvent ->
            val chrononBlocks = event.register(TempadCapabilities.Block.chronons)
            val chrononItems = event.register(TempadCapabilities.Item.chronons)
            val upgradeBlocks = event.register(TempadCapabilities.Block.upgrades)
            val upgradeItems = event.register(TempadCapabilities.Item.upgrades)
            val accessItems = event.register(PlayerLocationAccess.item)
            val blockItems = event.register(Capabilities.Item.BLOCK)
            val itemItems = event.register(Capabilities.Item.ITEM)

            chrononBlocks[ModBlocks.timedoorProjectorBE] = { it, _ ->
                (it as? RudimentaryTempadBE)?.chronons
            }

            chrononBlocks[ModBlocks.workstationBE] = { it, _ ->
                (it as? WorkstationBE)?.inventory?.access(0)?.chronons
            }

            /*
            chrononBlocks[ModBlocks.metronomeBe] = { it, _ ->
                safeLet(it as? MetronomeBe, (it as? MetronomeBe)?.owner) { block, owner ->
                    if (block.bootTime > 0) {
                        BlockChrononContent.metronome(block, CommonConfig.Metronome.jumpStartAmount)
                    } else {
                        if (server == null) return@safeLet BlockChrononContent.metronomeClient(block)
                        MultiversalChrononHandler(owner.id)
                    }
                }
            }
             */

            chrononItems[ModItems.timedoorProjector] = { stack, access ->
                ItemAccessEnergyHandler(access, ModComponents.chrononContent, CommonConfigCache.RudimentaryTempad.capacity)
            }

            chrononItems[ModItems.timeTwister] = { stack, access ->
                ItemAccessEnergyHandler(access, ModComponents.chrononContent, CommonConfigCache.TimeTwister.capacity)
            }

            chrononItems[ModItems.tempad] = { stack, access ->
                TempadChrononHandler.create(access, CommonConfigCache.Tempad.capacity, CommonConfigCache.TimeTwister.capacity)
            }

            chrononItems[ModItems.chrononCell] = { stack, access ->
                ItemAccessEnergyHandler(access, ModComponents.chrononContent, CommonConfigCache.ChrononCell.capacity)
            }

            chrononItems[ModItems.chrononBattery] = { stack, access ->
                ItemAccessEnergyHandler(access, ModComponents.chrononContent, CommonConfigCache.Battery.capacity)
            }

            chrononItems[ModItems.chronometer] = { stack, access ->
                ItemAccessEnergyHandler(access, ModComponents.chrononContent, CommonConfigCache.Chronometer.capacity)
            }

            chrononItems[ModItems.chrononGenerator] = { stack, access ->
                ItemAccessEnergyHandler(access, ModComponents.chrononContent, CommonConfigCache.ChrononGenerator.capacity)
            }

            chrononItems[ModItems.metronome] = { stack, access ->
                ItemAccessEnergyHandler(access, ModComponents.chrononContent, CommonConfigCache.Metronome.capacity)
            }

            chrononItems[ModItems.creativeChronometer] = { _, _ ->
                InfiniteEnergyHandler.INSTANCE
            }

            upgradeItems[ModItems.tempad] = { it, access ->
                ItemUpgradeHandler(it)
            }

            accessItems[ModItems.locationBroadcaster] = { it, _ ->
                if(it.enabled) DefaultLocationAccess.Public else null
            }

            accessItems[ModItems.screeningDevice] = { it, _ ->
                ScreeningDeviceLocationAccess.create(it)
            }

            upgradeBlocks[ModBlocks.workstationBE] = { it, _ ->
                (it as? WorkstationBE)?.inventory?.access(0)?.upgrades
            }

            blockItems[ModBlocks.metronomeBe] = { it, _ ->
                (it as? MetronomeBe)?.inventory
            }

            itemItems[ModItems.cardWallet] = { it, _ ->
                it.items
            }
        }

        bus.addListener { event: BuildCreativeModeTabContentsEvent ->
            if (event.tab !== ModItems.tab) return@addListener
            for (entry in ModItems.registry.entries) {
                event.accept(entry.get())
            }
        }

        bus.addListener { event: RegisterTicketControllersEvent ->
            event.register(ticketController)
        }

        bus.addListener { event: AddPackFindersEvent ->
            event.addPackFinders(
                "required_location_upgrade".tempadId,
                PackType.SERVER_DATA,
                Component.translatable("datapack.tempad.required_location_upgrade"),
                PackSource.FEATURE,
                false,
                Pack.Position.TOP
            )
        }

        // TODO very broken make sure to fix
        NeoForge.EVENT_BUS.addListener { event: RecipesReceivedEvent ->
            clientRecipes.clear()
            for (recipeHolder in event.recipeMap.byType(RecipeType.CRAFTING)) {
                val recipe = recipeHolder.value
                if (recipe is NormalCraftingRecipe && recipe.assemble(CraftingInput.EMPTY).id.namespace == MOD_ID) {
                    clientRecipes[recipeHolder.id.identifier()] = recipe
                }
            }
        }

        NeoForge.EVENT_BUS.addListener { event: PlayerTickEvent.Post ->
            if (event.entity.tickCount % 1200 != 0) return@addListener
            event.entity.travelHistory.logLocation(event.entity)
        }

        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST) { event: LivingDeathEvent ->
            if (event.isCanceled || event.entity !is Player) return@addListener
            event.entity.travelHistory.logLocation(event.entity, TravelHistoryAttachment.DEATH_MARKER)
        }

        NeoForge.EVENT_BUS.addListener { event: PlayerRespawnEvent ->
            event.entity.travelHistory.logLocation(event.entity, TravelHistoryAttachment.RESPAWN_MARKER)
        }

        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST) { event: EntityTravelToDimensionEvent ->
            val entity = event.entity
            if (event.isCanceled || entity !is Player) return@addListener
            entity.travelHistory.logLocation(entity, TravelHistoryAttachment.DIM_EXIT_MARKER)
        }

        NeoForge.EVENT_BUS.addListener { event: PlayerChangedDimensionEvent ->
            event.entity.travelHistory.logLocation(event.entity, TravelHistoryAttachment.DIM_ENTER_MARKER)
        }

        NeoForge.EVENT_BUS.addListener { event: ServerTickEvent.Post ->
            if(event.server.tickCount % CommonConfig.Metronome.generationRate == 0) {
                metronomeEnergy?.let {
                    it.tick()
                }
            }
        }

        NeoForge.EVENT_BUS.addListener { event: PlayerLoggedInEvent ->
            CommonConfigCache.CACHE.syncAll(event.entity)
        }

        NeoForge.EVENT_BUS.addListener { event: StartTracking ->
            if (event.entity.level().isClientSide) return@addListener
            (event.target as? TimedoorEntity)?.let {
                it.animationOffset = it.tickCount
            }
        }
    }
}