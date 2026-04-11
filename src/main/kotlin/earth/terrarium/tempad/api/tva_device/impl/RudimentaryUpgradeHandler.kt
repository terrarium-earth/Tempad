package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import net.minecraft.resources.Identifier

object RudimentaryUpgradeHandler: UpgradeHandler {
    override val installedUpgrades: List<Identifier> = listOf()

    override fun contains(upgrade: Identifier): Boolean = false

    override fun plusAssign(upgrade: Identifier) {}

    override fun minusAssign(upgrade: Identifier) {}

    override fun willAccept(upgrade: Identifier): Boolean = false

    override fun isRemovable(upgrade: Identifier): Boolean = false
}