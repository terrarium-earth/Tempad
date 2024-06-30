package earth.terrarium.tempad

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.location_handlers.WarpsHandler
import earth.terrarium.tempad.common.registries.*
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(Tempad.MOD_ID)
class Tempad(bus: IEventBus) {
    companion object {
        val String.tempadId: ResourceLocation
            get() = ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, this)

        const val MOD_ID = "tempad"
        val ORANGE: Color = Color(0xFF, 0x6f, 0, 255)
    }

    init {
        TempadLocations.register(DefaultLocationHandler.settings, DefaultLocationHandler)
        TempadLocations.register(WarpsHandler.SETTINGS, WarpsHandler)

        ModApps.init()
        ModAttachments.REGISTRY.init()
        ModComponents.REGISTRY.init()
        ModEntities.ENTITIES.init()
        ModEntities.DATA_SERIALIZERS.init()
        ModItems.REGISTRY.init()
        ModMacros.init()
        ModMenus.REGISTRY.init()
        ModNetworking.init()
    }
}