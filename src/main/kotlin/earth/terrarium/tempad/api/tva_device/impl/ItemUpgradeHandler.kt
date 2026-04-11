package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.api.tva_device.upgrades
import earth.terrarium.tempad.common.data.InstalledUpgradesComponent
import earth.terrarium.tempad.common.registries.installedUpgrades
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack

class ItemUpgradeHandler(val stack: ItemStack): UpgradeHandler {
    override var installedUpgrades: List<Identifier>
        get() = stack.installedUpgrades.upgrades
        set(value) {
            stack.installedUpgrades = InstalledUpgradesComponent(value)
        }

    override fun contains(upgrade: Identifier): Boolean {
        return upgrade in installedUpgrades
    }

    override fun plusAssign(upgrade: Identifier) {
        if (upgrade !in installedUpgrades) installedUpgrades += upgrade
    }

    override fun minusAssign(upgrade: Identifier) {
        installedUpgrades -= upgrade
    }

    override fun willAccept(upgrade: Identifier): Boolean {
        return true
    }

    override fun isRemovable(upgrade: Identifier): Boolean {
        return true
    }
}