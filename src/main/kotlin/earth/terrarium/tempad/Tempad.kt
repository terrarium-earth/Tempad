package earth.terrarium.tempad

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.data.locationData
import earth.terrarium.tempad.common.registries.ModAttachments
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.RegistryBuilder

@Mod(Tempad.MOD_ID)
object Tempad {
    val String.tempadId: ResourceLocation
        get() = ResourceLocation.fromNamespaceAndPath(Tempad.MOD_ID, this)

    const val MOD_ID = "tempad"
    val ORANGE: Color = Color(0xFF, 0x6f, 0, 255)
    val TEMPAD_PROVIDER_SETTINGS = ProviderSettings("main".tempadId)
    val APP_REGISTRY_ID: ResourceKey<Registry<TempadApp<*>>> = ResourceKey.createRegistryKey("app".tempadId)
    val APP_REGISTRY: Registry<TempadApp<*>> = RegistryBuilder(APP_REGISTRY_ID).create()

    init {
        ModAttachments.ATTACHMENTS.init()
        TempadLocations.register(TEMPAD_PROVIDER_SETTINGS) { it.locationData.locations }
    }
}