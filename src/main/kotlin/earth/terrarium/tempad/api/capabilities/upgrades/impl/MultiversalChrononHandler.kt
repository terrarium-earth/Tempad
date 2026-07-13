package earth.terrarium.tempad.api.capabilities.upgrades.impl

import earth.terrarium.tempad.common.registries.metronomeEnergy
import net.neoforged.neoforge.transfer.energy.EnergyHandler
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal
import net.neoforged.neoforge.transfer.transaction.TransactionContext
import java.util.UUID
import kotlin.collections.set

class MultiversalChrononHandler(val playerId: UUID) : EnergyHandler, SnapshotJournal<Int>() {
    var energy: Int = metronomeEnergy?.stored[playerId] ?: 0
    val capacity: Int = metronomeEnergy?.getCapacity(playerId) ?: 0

    override fun extract(amount: Int, transaction: TransactionContext): Int {
        val oldPower = amountAsInt
        energy = (amountAsInt - amount).coerceAtLeast(0)
        return oldPower - amountAsInt
    }

    override fun insert(amount: Int, transaction: TransactionContext): Int {
        val oldPower = amountAsInt
        energy = (amountAsInt + amount).coerceAtMost(capacityAsInt)
        return amountAsInt - oldPower
    }

    override fun getAmountAsLong(): Long = energy.toLong()

    override fun getCapacityAsLong(): Long = capacity.toLong()

    override fun createSnapshot(): Int = energy

    override fun revertToSnapshot(snapshot: Int) {
        energy = snapshot
    }

    override fun onRootCommit(originalState: Int) {
        metronomeEnergy?.stored[playerId] = energy
    }
}