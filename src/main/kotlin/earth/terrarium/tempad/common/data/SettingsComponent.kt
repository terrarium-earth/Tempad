package earth.terrarium.tempad.common.data

import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.ModMacros
import earth.terrarium.tempad.common.utils.ComponentDelegate
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.MutableDataComponentHolder
import com.teamresourceful.bytecodecs.defaults.EnumCodec as EnumByteCodec

enum class OrganizationMethod {
    BY_PROVIDER,
    BY_DIMENSION,
    ALPHABETICAL,
}

data class SettingsComponent(val defaultApp: ResourceLocation, val defaultMacro: ResourceLocation, val organizationMethod: OrganizationMethod) {

    companion object {
        val CODEC = RecordCodecBuilder.create {
            it.group(
                ResourceLocation.CODEC.fieldOf("defaultApp").forGetter(SettingsComponent::defaultApp),
                ResourceLocation.CODEC.fieldOf("defaultMacro").forGetter(SettingsComponent::defaultMacro),
                EnumCodec.of(OrganizationMethod::class.java).fieldOf("organizationMethod").forGetter(SettingsComponent::organizationMethod),
            ).apply(it, ::SettingsComponent)
        }

        val BYTE_CODEC = ObjectByteCodec.create(
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.defaultApp },
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.defaultMacro },
            EnumByteCodec(OrganizationMethod::class.java).fieldOf { it.organizationMethod },
            ::SettingsComponent
        )
    }
}
