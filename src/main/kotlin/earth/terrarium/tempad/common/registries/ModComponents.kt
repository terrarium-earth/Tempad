package earth.terrarium.tempad.common.registries

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.data.OrganizationMethod
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.MutableDataComponentHolder

object ModComponents {
    val registry: ResourcefulRegistry<DataComponentType<*>> =
        ResourcefulRegistries.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Tempad.MOD_ID)

    val defaultApp: DataComponentType<ResourceLocation> by registry.register("default_app") {
        componentType {
            serialize = ResourceLocation.CODEC
            networkSerialize = ExtraByteCodecs.RESOURCE_LOCATION
        }
    }

    val defaultMacro: DataComponentType<ResourceLocation> by registry.register("default_macro") {
        componentType {
            serialize = ResourceLocation.CODEC
            networkSerialize = ExtraByteCodecs.RESOURCE_LOCATION
        }
    }

    val organizationMethod: DataComponentType<OrganizationMethod> by registry.register("organization_method") {
        componentType {
            serialize = OrganizationMethod.CODEC
            networkSerialize = OrganizationMethod.BYTE_CODEC
        }
    }

    val fuelCharges: DataComponentType<Int> by registry.register("fuel_charges") {
        componentType {
            serialize = Codec.INT
            networkSerialize = ByteCodec.INT
        }
    }

    val fuelType: DataComponentType<ResourceLocation> by registry.register("fuel_type") {
        componentType {
            serialize = ResourceLocation.CODEC
            networkSerialize = ExtraByteCodecs.RESOURCE_LOCATION
        }
    }

    val energyConsumeAmount: DataComponentType<Int> by registry.register("energy_consume_amount") {
        componentType {
            serialize = Codec.INT
            networkSerialize = ByteCodec.INT
        }
    }

    val expConsumeAmount: DataComponentType<Int> by registry.register("experience_consume_amount") {
        componentType {
            serialize = Codec.INT
            networkSerialize = ByteCodec.INT
        }
    }

    val fluidContent: DataComponentType<Int> by registry.register("chronon_content") {
        componentType {
            serialize = Codec.INT
            networkSerialize = ByteCodec.INT
        }
    }

    val staticLocation: DataComponentType<LocationData> by registry.register("static_location") {
        componentType {
            serialize = LocationData.CODEC
            networkSerialize = LocationData.BYTE_CODEC
        }
    }

    val enabled: DataComponentType<Boolean> by registry.register("enabled") {
        componentType {
            serialize = Codec.BOOL
            networkSerialize = ByteCodec.BOOLEAN
        }
    }
}

var MutableDataComponentHolder.fuelCharges by ModComponents.fuelCharges.default(0)

var MutableDataComponentHolder.fuelType by ModComponents.fuelType

var MutableDataComponentHolder.energyConsumeAmount by ModComponents.energyConsumeAmount.default(CommonConfigCache.energyPerCharge)

var MutableDataComponentHolder.experienceConsumeAmount by ModComponents.expConsumeAmount.default(CommonConfigCache.expPerCharge)

var MutableDataComponentHolder.defaultApp by ModComponents.defaultApp.default(ModApps.teleport)

var MutableDataComponentHolder.defaultMacro by ModComponents.defaultMacro.default(ModMacros.teleportToPinned)

var MutableDataComponentHolder.organizationMethod by ModComponents.organizationMethod.default(OrganizationMethod.DIMENSION)

var MutableDataComponentHolder.chrononContent by ModComponents.fluidContent.default(0)

var MutableDataComponentHolder.staticLocation by ModComponents.staticLocation

var MutableDataComponentHolder.enabled by ModComponents.enabled.default(true)