package earth.terrarium.tempad.common.data

import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.tooltip.TooltipComponent

data class InstalledUpgradesComponent(val upgrades: List<ResourceLocation>): TooltipComponent {
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

    operator fun contains(upgrade: ResourceLocation): Boolean {
        return upgrade in upgrades
    }
}