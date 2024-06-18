package earth.terrarium.tempad

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.api.TempadLocations
import earth.terrarium.tempad.api.ProviderSettings
import earth.terrarium.tempad.common.data.locationData
import earth.terrarium.tempad.common.registries.ModAttachments
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod

@Mod(Tempad.MOD_ID)
object Tempad {
    val String.resourceLocation: ResourceLocation
        get() = ResourceLocation(Tempad.MOD_ID, this)

    const val MOD_ID = "tempad"
    val ORANGE: Color = Color(0xFF, 0x6f, 0, 255)
    val TEMPAD_PROVIDER_SETTINGS = ProviderSettings("main".resourceLocation)

    init {
        ModAttachments.ATTACHMENTS.init()
        TempadLocations.register(TEMPAD_PROVIDER_SETTINGS) { it.locationData.locations }
    }
}