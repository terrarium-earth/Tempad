package earth.terrarium.tempad.common.registries

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.componentType
import earth.terrarium.tempad.common.utils.networkSerialize
import earth.terrarium.tempad.common.utils.serialize
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

object ModComponents {
    val REGISTRY = BuiltInRegistries.DATA_COMPONENT_TYPE.createRegistry(Tempad.MOD_ID)

    val DEFAULT_APP by REGISTRY.register("default_app") {
        componentType<ResourceLocation> {
            serialize = ResourceLocation.CODEC
            networkSerialize = ExtraByteCodecs.RESOURCE_LOCATION
        }
    }

    val DEFAULT_MACRO by REGISTRY.register("default_macro") {
        componentType<ResourceLocation> {
            serialize = ResourceLocation.CODEC
            networkSerialize = ExtraByteCodecs.RESOURCE_LOCATION
        }
    }
}