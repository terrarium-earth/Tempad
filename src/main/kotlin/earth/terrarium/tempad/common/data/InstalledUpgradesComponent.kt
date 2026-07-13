package earth.terrarium.tempad.common.data

import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import net.minecraft.resources.Identifier
import net.minecraft.world.inventory.tooltip.TooltipComponent

data class InstalledUpgradesComponent(val upgrades: List<Identifier>): TooltipComponent {
    companion object {
        val codec = RecordCodecBuilder.create { it ->
            it.group(
                Identifier.CODEC.listOf().fieldOf("upgrades").forGetter(InstalledUpgradesComponent::upgrades)
            ).apply(it, ::InstalledUpgradesComponent)
        }

        val byteCodec = ObjectByteCodec.create(
            ExtraByteCodecs.IDENTIFIER.listOf().fieldOf { it.upgrades },
            ::InstalledUpgradesComponent
        )
    }

    operator fun plus(upgrade: Identifier): InstalledUpgradesComponent {
        return InstalledUpgradesComponent(upgrades + upgrade)
    }

    operator fun minus(upgrade: Identifier): InstalledUpgradesComponent {
        return InstalledUpgradesComponent(upgrades - upgrade)
    }

    operator fun contains(upgrade: Identifier): Boolean {
        return upgrade in upgrades
    }
}