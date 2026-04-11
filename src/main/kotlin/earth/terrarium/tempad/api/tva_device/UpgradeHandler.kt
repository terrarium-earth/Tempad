package earth.terrarium.tempad.api.tva_device

import earth.terrarium.tempad.tempadId
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability

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

    companion object Capabilities {
        val block = BlockCapability.createVoid("upgrades".tempadId, UpgradeHandler::class.java)
        val item = ItemCapability.createVoid("upgrades".tempadId, UpgradeHandler::class.java)
    }
}

val ItemStack.upgrades: UpgradeHandler? get() = getCapability(UpgradeHandler.Capabilities.item)

val BlockEntity.upgrades: UpgradeHandler?
    get() = level!!.getCapability(
        UpgradeHandler.Capabilities.block,
        blockPos,
        null
    )