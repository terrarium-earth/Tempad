package earth.terrarium.tempad.common.registries

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.data.SettingsComponent
import earth.terrarium.tempad.common.utils.componentType
import earth.terrarium.tempad.common.utils.networkSerialize
import earth.terrarium.tempad.common.utils.serialize
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

object ModComponents {
    val REGISTRY: ResourcefulRegistry<DataComponentType<*>> = ResourcefulRegistries.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Tempad.MOD_ID)

    val TEMPAD_SETTINGS: DataComponentType<SettingsComponent> by REGISTRY.register("tempad_settings") {
        componentType {
            serialize = SettingsComponent.CODEC
            networkSerialize = SettingsComponent.BYTE_CODEC
        }
    }
}