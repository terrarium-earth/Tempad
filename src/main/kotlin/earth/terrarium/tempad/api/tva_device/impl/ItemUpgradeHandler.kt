package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.common.data.InstalledUpgradesComponent
import earth.terrarium.tempad.common.registries.installedUpgrades
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class ItemUpgradeHandler(val stack: ItemStack): UpgradeHandler {
    override var installedUpgrades: List<ResourceLocation>
        get() = stack.installedUpgrades.upgrades
        set(value) {
            stack.installedUpgrades = InstalledUpgradesComponent(value)
        }

    override fun contains(upgrade: ResourceLocation): Boolean {
        return upgrade in installedUpgrades
    }

    override fun plusAssign(upgrade: ResourceLocation) {
        installedUpgrades += upgrade
    }

    override fun minusAssign(upgrade: ResourceLocation) {
        installedUpgrades -= upgrade
    }

    override fun willAccept(upgrade: ResourceLocation): Boolean {
        return true
    }

    override fun isRemovable(upgrade: ResourceLocation): Boolean {
        TODO("Not yet implemented")
    }
}