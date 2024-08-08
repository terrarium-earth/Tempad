package earth.terrarium.tempad.common.data

import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import net.minecraft.resources.ResourceLocation

data class InstalledUpgradesComponent(val upgrades: List<ResourceLocation>) {
    companion object {
        val codec = RecordCodecBuilder.create { it ->
            it.group(
                ResourceLocation.CODEC.listOf().fieldOf("upgrades").forGetter(InstalledUpgradesComponent::upgrades)
            ).apply(it, ::InstalledUpgradesComponent)
        }

        val byteCodec = ObjectByteCodec.create(
            ExtraByteCodecs.RESOURCE_LOCATION.listOf().fieldOf { it.upgrades },
            ::InstalledUpgradesComponent
        )
    }

    operator fun plus(upgrade: ResourceLocation): InstalledUpgradesComponent {
        return InstalledUpgradesComponent(upgrades + upgrade)
    }

    operator fun minus(upgrade: ResourceLocation): InstalledUpgradesComponent {
        return InstalledUpgradesComponent(upgrades - upgrade)
    }
}