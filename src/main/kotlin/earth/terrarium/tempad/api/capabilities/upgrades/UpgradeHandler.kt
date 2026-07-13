package earth.terrarium.tempad.api.capabilities.upgrades

import net.minecraft.resources.Identifier

interface UpgradeHandler {
    val installedUpgrades: List<Identifier>

    operator fun contains(upgrade: Identifier): Boolean

    operator fun plusAssign(upgrade: Identifier)

    operator fun minusAssign(upgrade: Identifier)

    fun install(upgrade: Identifier) {
        this += upgrade
    }

    fun uninstall(upgrade: Identifier) {
        this -= upgrade
    }

    fun willAccept(upgrade: Identifier): Boolean

    fun isRemovable(upgrade: Identifier): Boolean
}