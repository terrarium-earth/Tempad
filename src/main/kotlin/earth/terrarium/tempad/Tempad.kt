package earth.terrarium.tempad

import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.api.player_access.DefaultAccess
import earth.terrarium.tempad.api.player_access.PlayerAccess
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.impl.RudimentaryChrononContent
import earth.terrarium.tempad.api.tva_device.impl.InfiniteChrononHandler
import earth.terrarium.tempad.api.tva_device.impl.ItemChrononHandler
import earth.terrarium.tempad.api.tva_device.impl.ItemUpgradeHandler
import earth.terrarium.tempad.api.tva_device.impl.MultiversalChrononHandler
import earth.terrarium.tempad.api.tva_device.impl.RudimentaryUpgradeHandler
import earth.terrarium.tempad.api.tva_device.impl.TempadChrononHandler
import earth.terrarium.tempad.api.tva_device.impl.WorkstationChrononHandler
import earth.terrarium.tempad.api.tva_device.upgrades
import earth.terrarium.tempad.common.block.MetronomeBe
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import earth.terrarium.tempad.common.block.WorkstationBE
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.data.TravelHistoryAttachment
import earth.terrarium.tempad.common.items.ScreeningDeviceAccess
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.register
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent
import net.neoforged.neoforge.common.world.chunk.TicketController
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val String.tempadId: ResourceLocation
    get() = ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, this)

@Mod(Tempad.MOD_ID)
class Tempad(bus: IEventBus) {
    companion object {

        const val MOD_ID = "tempad"
        val DARK_ORANGE: Color = Color(0x91, 0x45, 0x0d, 255)
        val ORANGE: Color = Color(0xFF, 0x6f, 0, 255)
        val HIGHLIGHTED_ORANGE: Color = Color(0xFF, 0xcc, 0x54, 255)
        val GREEN = Color(0xFF65fb01.toInt())

        val CONFIGURATOR = Configurator(MOD_ID)

        val server: MinecraftServer?
            get() = ServerLifecycleHooks.getCurrentServer();

        val playerUpgrade: ResourceLocation = "player".tempadId

        val logger: Logger = LogManager.getLogger(MOD_ID)

        val ticketController = TicketController("timedoor".tempadId, null)
    }

    init {
        CONFIGURATOR.register(CommonConfig::class.java)

        ModApps.init()
        ModSizing.init()
        ModAttachments.registry.init()
        ModAttachments.syncer.init()
        ModComponents.registry.init()
        ModContext.init()
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

        bus.addListener { event: RegisterCapabilitiesEvent ->
            val chrononBlocks = event.register(ChrononHandler.block)
            val chrononItems = event.register(ChrononHandler.item)
            val upgradeItems = event.register(UpgradeHandler.item)
            val upgradeBlocks = event.register(UpgradeHandler.block)
            val accessItems = event.register(PlayerAccess.item)

            chrononBlocks[ModBlocks.timedoorProjectorBE] = { it, _ ->
                (it as? RudimentaryTempadBE)?.let {
                    RudimentaryChrononContent.create(it, CommonConfigCache.RudimentaryTempad.capacity)
                }
            }

            chrononBlocks[ModBlocks.workstationBE] = { it, _ ->
                if((it as? WorkstationBE)?.inventory[0]?.chronons != null) {
                    WorkstationChrononHandler(it)
                } else null
            }

            chrononBlocks[ModBlocks.metronomeBe] = { it, _ ->
                (it as? MetronomeBe)?.owner?.let {
                    MultiversalChrononHandler(it.id)
                }
            }

            chrononItems[ModItems.timedoorProjector] = { stack, _ ->
                ItemChrononHandler.create(stack, CommonConfigCache.RudimentaryTempad.capacity)?.apply { canExtract = false }
            }

            chrononItems[ModItems.timeTwister] = { stack, _ ->
                ItemChrononHandler.create(stack, CommonConfigCache.TimeTwister.capacity)?.apply { canExtract = false }
            }

            chrononItems[ModItems.tempad] = { stack, _ ->
                TempadChrononHandler.create(stack, CommonConfigCache.Tempad.capacity, CommonConfigCache.TimeTwister.capacity)
            }

            chrononItems[ModItems.chrononCell] = { stack, _ ->
                ItemChrononHandler.create(stack, CommonConfigCache.Capacitor.capacity)
            }

            chrononItems[ModItems.chrononBattery] = { stack, _ ->
                ItemChrononHandler.create(stack, CommonConfigCache.Battery.capacity)
            }

            chrononItems[ModItems.chronometer] = { stack, _ ->
                ItemChrononHandler.create(stack, CommonConfigCache.Chronometer.capacity)?.apply { canInsert = false }
            }

            chrononItems[ModItems.chrononGenerator] = { stack, _ ->
                ItemChrononHandler.create(stack, CommonConfigCache.ChrononGenerator.capacity)?.apply { canInsert = false }
            }

            chrononItems[ModItems.metronome] = { stack, _ ->
                ItemChrononHandler.create(stack, CommonConfigCache.Metronome.capacity)
            }

            chrononItems[ModItems.creativeChronometer] = { _, _ ->
                InfiniteChrononHandler
            }

            upgradeItems[ModItems.tempad] = { it, _ ->
                ItemUpgradeHandler(it)
            }

            upgradeItems[ModItems.timedoorProjector] = { it, _ ->
                RudimentaryUpgradeHandler
            }

            accessItems[ModItems.locationBroadcaster] = { it, _ ->
                if(it.enabled == true) DefaultAccess.Public else null
            }

            accessItems[ModItems.screeningDevice] = { it, _ ->
                ScreeningDeviceAccess.create(it)
            }

            upgradeBlocks[ModBlocks.workstationBE] = { it, _ ->
                (it as? WorkstationBE)?.inventory?.getStackInSlot(0)?.upgrades
            }

            upgradeBlocks[ModBlocks.timedoorProjectorBE] = { it, _ ->
                RudimentaryUpgradeHandler
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
    }
}