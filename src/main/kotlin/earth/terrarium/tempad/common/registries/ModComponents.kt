package earth.terrarium.tempad.common.registries

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.common.data.InstalledUpgradesComponent
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.MutableDataComponentHolder
import java.util.UUID

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

    val chrononContentTempad: DataComponentType<Int> by registry.register("chronon_content_tempad") {
        componentType {
            serialize = Codec.INT
            networkSerialize = ByteCodec.INT
        }
    }

    val chrononContentTimeTwister : DataComponentType<Int> by registry.register("chronon_content_time_twister") {
        componentType {
            serialize = Codec.INT
            networkSerialize = ByteCodec.INT
        }
    }

    val twisterData: DataComponentType<DataComponentPatch> by registry.register("twister_data") {
        componentType {
            serialize = DataComponentPatch.CODEC
            networkSynchronized(DataComponentPatch.STREAM_CODEC)
        }
    }

    val targetPos: DataComponentType<NamedGlobalVec3> by registry.register("target_position") {
        componentType {
            serialize = NamedGlobalVec3.CODEC.codec()
            networkSerialize = NamedGlobalVec3.BYTE_CODEC
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

    val anchorId: DataComponentType<UUID> by registry.register("anchor_id") {
        componentType {
            serialize = UUIDUtil.STRING_CODEC
            networkSerialize = ByteCodec.UUID
        }
    }
}

var MutableDataComponentHolder.defaultApp by ModComponents.defaultApp.withDefault(ModApps.teleport)

var MutableDataComponentHolder.defaultMacro by ModComponents.defaultMacro.withDefault(ModMacros.teleportToPinned)

var MutableDataComponentHolder.chrononContent by ModComponents.chrononContent.withDefault(0)

var MutableDataComponentHolder.chrononContentTempad by ModComponents.chrononContentTempad.withDefault(0)

var MutableDataComponentHolder.chrononContentTimeTwister by ModComponents.chrononContentTimeTwister.withDefault(0)

var MutableDataComponentHolder.twisterData by ModComponents.twisterData

var MutableDataComponentHolder.targetPos by ModComponents.targetPos

var MutableDataComponentHolder.enabled by ModComponents.enabled.withDefault(true)

var MutableDataComponentHolder.twisterEquipped by ModComponents.twisterEquipped.withDefault(false)

var MutableDataComponentHolder.installedUpgrades by ModComponents.installedUpgrades.withDefault(InstalledUpgradesComponent(emptyList()))

var MutableDataComponentHolder.color by ModComponents.color

var MutableDataComponentHolder.anchorId by ModComponents.anchorId