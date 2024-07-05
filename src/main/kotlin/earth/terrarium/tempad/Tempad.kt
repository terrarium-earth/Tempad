package earth.terrarium.tempad

import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.api.fuel.FuelConsumer
import earth.terrarium.tempad.api.fuel.FuelRegistry
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.location_handlers.WarpsHandler
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.register
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.server.ServerLifecycleHooks

@Mod(Tempad.MOD_ID)
class Tempad(bus: IEventBus) {
    companion object {
        val String.tempadId: ResourceLocation
            get() = ResourceLocation.fromNamespaceAndPath(MOD_ID, this)

        const val MOD_ID = "tempad"
        val DARK_ORANGE: Color = Color(0x91, 0x45, 0x0d, 255)
        val ORANGE: Color = Color(0xFF, 0x6f, 0, 255)
        val HIGHLIGHTED_ORANGE: Color = Color(0xFF, 0xcc, 0x54, 255)

        val CONFIGURATOR = Configurator(MOD_ID)

        val server: MinecraftServer?
            get() = ServerLifecycleHooks.getCurrentServer();
    }

    init {
        TempadLocations[DefaultLocationHandler.SETTINGS] = ::DefaultLocationHandler

        TempadLocations.set(WarpsHandler.SETTINGS, WarpsHandler)

        CONFIGURATOR.register(CommonConfig::class.java)

        ModApps.init()
        ModAttachments.REGISTRY.init()
        ModComponents.REGISTRY.init()
        ModEntities.ENTITIES.init()
        ModEntities.DATA_SERIALIZERS.init()
        ModItems.REGISTRY.init()
        ModMacros.init()
        ModMenus.REGISTRY.init()
        ModNetworking.init()
        ModFuel.init()
    }

    @SubscribeEvent
    fun registerCaps(event: RegisterCapabilitiesEvent) {
        event.register(FuelConsumer.CAPABILITY)[ModItems.TEMPAD] = { stack, _ ->
            FuelRegistry.get(
                stack.fuelType ?: ResourceLocation.tryParse(CommonConfigCache.Tempad.fuelType) ?: ModFuel.INFINITE,
                stack,
                CommonConfigCache.Tempad.capacity
            )
        }

        event.register(FuelConsumer.CAPABILITY)[ModItems.ADVANCED_TEMPAD] = { stack, _ ->
            FuelRegistry.get(
                stack.fuelType ?: ResourceLocation.tryParse(CommonConfigCache.AdvancedTempad.fuelType) ?: ModFuel.INFINITE,
                stack,
                CommonConfigCache.AdvancedTempad.capacity
            )
        }

        event.register(Capabilities.ItemHandler.ITEM)[ModItems.TEMPAD, ModItems.ADVANCED_TEMPAD] = { stack, _ ->
            val fuelConsumer = stack[FuelConsumer.CAPABILITY]
            if (fuelConsumer is IItemHandler) fuelConsumer else null
        }

        event.register(Capabilities.FluidHandler.ITEM)[ModItems.TEMPAD, ModItems.ADVANCED_TEMPAD] = { stack, _ ->
            val fuelConsumer = stack[FuelConsumer.CAPABILITY]
            if (fuelConsumer is IFluidHandlerItem) fuelConsumer else null
        }

        event.register(Capabilities.EnergyStorage.ITEM)[ModItems.TEMPAD, ModItems.ADVANCED_TEMPAD] = { stack, _ ->
            val fuelConsumer = stack[FuelConsumer.CAPABILITY]
            if (fuelConsumer is IEnergyStorage) fuelConsumer else null
        }
    }
}