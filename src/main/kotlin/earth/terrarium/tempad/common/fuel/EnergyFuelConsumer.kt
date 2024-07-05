package earth.terrarium.tempad.common.fuel

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.energy.IEnergyStorage

class EnergyFuelConsumer(stack: ItemStack, override val totalCharges: Int) : BaseFuelConsumer(stack), IEnergyStorage {
    companion object {
        const val consumeAmount = 1000
    }

    override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
        if (toReceive <= 0 || charges >= totalCharges) return 0

        val amount = (toReceive / consumeAmount).coerceAtMost(totalCharges - charges)
        if (!simulate) this += amount
        return amount * consumeAmount
    }

    override fun extractEnergy(toExtract: Int, simulate: Boolean): Int = 0

    override fun getEnergyStored(): Int = 0

    override fun getMaxEnergyStored(): Int = totalCharges * consumeAmount

    override fun canExtract(): Boolean = false

    override fun canReceive(): Boolean = true
}