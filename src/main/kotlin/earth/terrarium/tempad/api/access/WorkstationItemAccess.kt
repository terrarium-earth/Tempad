package earth.terrarium.tempad.api.access

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.utils.access
import earth.terrarium.tempad.common.utils.insert
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.transaction.TransactionContext
import kotlin.jvm.optionals.getOrNull

class WorkstationItemAccess(val level: Level, val blockPos: BlockPos): ItemAccess {
    val workstation = level.getBlockEntity(blockPos, ModBlocks.workstationBE).getOrNull()
    val access = workstation?.inventory?.access(0)

    override fun getResource(): ItemResource = access?.resource ?: ItemResource.EMPTY

    override fun getAmount(): Int = access?.amount ?: 0

    override fun insert(
        resource: ItemResource,
        amount: Int,
        transaction: TransactionContext,
    ): Int {
        return access?.insert(resource, amount, transaction) ?: 0
    }

    override fun extract(
        resource: ItemResource,
        amount: Int,
        transaction: TransactionContext,
    ): Int {
        return access?.extract(resource, amount, transaction) ?: 0
    }

    override fun exchange(newResource: ItemResource, amount: Int, transaction: TransactionContext?): Int {
        return access?.exchange(newResource, amount, transaction) ?: 0
    }
}