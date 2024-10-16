package earth.terrarium.tempad.api.tva_device

import earth.terrarium.tempad.tempadId
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability

interface UpgradeHandler {
    val installedUpgrades: List<ResourceLocation>

    operator fun contains(upgrade: ResourceLocation): Boolean

    operator fun plusAssign(upgrade: ResourceLocation)

    operator fun minusAssign(upgrade: ResourceLocation)

    fun install(upgrade: ResourceLocation) {
        this += upgrade
    }

    fun uninstall(upgrade: ResourceLocation) {
        this -= upgrade
    }

    fun willAccept(upgrade: ResourceLocation): Boolean

    fun isRemovable(upgrade: ResourceLocation): Boolean

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