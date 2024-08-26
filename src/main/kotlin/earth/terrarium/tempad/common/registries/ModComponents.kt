package earth.terrarium.tempad.common.registries

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.common.data.InstalledUpgradesComponent
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
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

    val chrononContent: DataComponentType<Int> by registry.register("chronon_content") {
        componentType {
            serialize = Codec.INT
            networkSerialize = ByteCodec.INT
        }
    }

    val targetPos: DataComponentType<NamedGlobalPos> by registry.register("target_position") {
        componentType {
            serialize = NamedGlobalPos.codec
            networkSerialize = NamedGlobalPos.byteCodec
        }
    }

    val enabled: DataComponentType<Boolean> by registry.register("enabled") {
        componentType {
            serialize = Codec.BOOL
            networkSerialize = ByteCodec.BOOLEAN
        }
    }

    val twisterEquipped: DataComponentType<Boolean> by registry.register("twister_equipped") {
        componentType {
            serialize = Codec.BOOL
            networkSerialize = ByteCodec.BOOLEAN
        }
    }

    val installedUpgrades: DataComponentType<InstalledUpgradesComponent> by registry.register("installed_upgrades") {
        componentType {
            serialize = InstalledUpgradesComponent.codec
            networkSerialize = InstalledUpgradesComponent.byteCodec
        }
    }

    val color: DataComponentType<Color> by registry.register("color") {
        componentType {
            serialize = Color.CODEC
            networkSerialize = Color.BYTE_CODEC
        }
    }
}

var MutableDataComponentHolder.defaultApp by ModComponents.defaultApp.withDefault(ModApps.teleport)

var MutableDataComponentHolder.defaultMacro by ModComponents.defaultMacro.withDefault(ModMacros.teleportToPinned)

var MutableDataComponentHolder.chrononContent by ModComponents.chrononContent.withDefault(0)

var MutableDataComponentHolder.targetPos by ModComponents.targetPos

var MutableDataComponentHolder.enabled by ModComponents.enabled.withDefault(true)

var MutableDataComponentHolder.twisterEquipped by ModComponents.twisterEquipped.withDefault(false)

var MutableDataComponentHolder.installedUpgrades by ModComponents.installedUpgrades.withDefault(InstalledUpgradesComponent(emptyList()))

var MutableDataComponentHolder.color by ModComponents.color