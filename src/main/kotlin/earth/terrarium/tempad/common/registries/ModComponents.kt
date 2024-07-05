package earth.terrarium.tempad.common.registries

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.OrganizationMethod
import earth.terrarium.tempad.common.data.SettingsComponent
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.MutableDataComponentHolder

object ModComponents {
    val REGISTRY: ResourcefulRegistry<DataComponentType<*>> =
        ResourcefulRegistries.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Tempad.MOD_ID)

    val TEMPAD_SETTINGS: DataComponentType<SettingsComponent> by REGISTRY.register("tempad_settings") {
        componentType {
            serialize = SettingsComponent.CODEC
            networkSerialize = SettingsComponent.BYTE_CODEC
        }
    }

    val FUEL_CHARGES: DataComponentType<Int> by REGISTRY.register("fuel_charges") {
        componentType {
            serialize = Codec.INT
            networkSerialize = ByteCodec.INT
        }
    }

    val TEMPAD_FUEL_TYPE: DataComponentType<ResourceLocation> by REGISTRY.register("fuel_type") {
        componentType {
            serialize = ResourceLocation.CODEC
            networkSerialize = ExtraByteCodecs.RESOURCE_LOCATION
        }
    }
}

var MutableDataComponentHolder.settings by ModComponents.TEMPAD_SETTINGS.default(
    SettingsComponent(
        ModApps.TELEPORT,
        ModMacros.DEFAULT_MACRO_ID,
        OrganizationMethod.BY_PROVIDER
    )
)

var MutableDataComponentHolder.fuelCharges by ModComponents.FUEL_CHARGES.default(0)

var MutableDataComponentHolder.fuelType by ModComponents.TEMPAD_FUEL_TYPE