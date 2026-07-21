package earth.terrarium.tempad.common.registries

import com.mojang.serialization.Codec
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.DimensionTpData
import earth.terrarium.tempad.tempadId
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.NeoForgeRegistries
import net.neoforged.neoforge.registries.datamaps.DataMapType
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent

@EventBusSubscriber(modid = Tempad.MOD_ID)
object ModDataMaps {
    val dimensionDistance = DataMapType.builder(
        "dimension_distance".tempadId,
        Registries.DIMENSION_TYPE,
        DimensionTpData.codec
    ).build()

    @SubscribeEvent
    fun registerModDataMaps(event: RegisterDataMapTypesEvent) {
        event.register(dimensionDistance)
    }
}