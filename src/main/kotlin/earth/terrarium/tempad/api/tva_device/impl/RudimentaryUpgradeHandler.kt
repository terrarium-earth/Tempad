package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import net.minecraft.resources.ResourceLocation

object RudimentaryUpgradeHandler: UpgradeHandler {
    override val installedUpgrades: List<ResourceLocation> = listOf()

    override fun contains(upgrade: ResourceLocation): Boolean = false

    override fun plusAssign(upgrade: ResourceLocation) {}

    override fun minusAssign(upgrade: ResourceLocation) {}

    override fun willAccept(upgrade: ResourceLocation): Boolean = false

    override fun isRemovable(upgrade: ResourceLocation): Boolean = false
}