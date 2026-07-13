package earth.terrarium.tempad.api.capabilities

import earth.terrarium.tempad.api.capabilities.player_access.PlayerLocationAccess
import earth.terrarium.tempad.api.capabilities.upgrades.UpgradeHandler
import earth.terrarium.tempad.tempadId
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.energy.EnergyHandler

object TempadCapabilities {
    object Item {
        val chronons = ItemCapability.create("chronons".tempadId, EnergyHandler::class.java, ItemAccess::class.java)
        val upgrades = ItemCapability.create("upgrades".tempadId, UpgradeHandler::class.java, ItemAccess::class.java)
        val playerLocationAccess = ItemCapability.create("player_access".tempadId, PlayerLocationAccess::class.java, ItemAccess::class.java)
    }

    object Block {
        val chronons = BlockCapability.createSided("chronons".tempadId, EnergyHandler::class.java)
        val upgrades = BlockCapability.createSided("upgrades".tempadId, UpgradeHandler::class.java)

    }
}

val ItemAccess.upgrades: UpgradeHandler? get() = getCapability(TempadCapabilities.Item.upgrades)

val BlockEntity.upgrades: UpgradeHandler?
    get() = level!!.getCapability(
        TempadCapabilities.Block.upgrades,
        blockPos,
        null
    )

val ItemAccess.chronons: EnergyHandler? get() = getCapability(TempadCapabilities.Item.chronons)

val BlockEntity.chronons: EnergyHandler?
    get() = level!!.getCapability(
        TempadCapabilities.Block.chronons,
        blockPos,
        null
    )

val ItemAccess.playerLocationAccess: PlayerLocationAccess? get() = getCapability(TempadCapabilities.Item.playerLocationAccess)