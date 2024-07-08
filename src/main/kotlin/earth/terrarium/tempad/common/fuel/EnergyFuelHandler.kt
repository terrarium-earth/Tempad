package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.api.fuel.ItemContext
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.registries.energyConsumeAmount
import earth.terrarium.tempad.common.utils.get
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage

class EnergyFuelHandler(stack: ItemStack, override val totalCharges: Int) : BaseFuelHandler(stack, "energy"), IEnergyStorage {
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

    override fun addChargeFromItem(context: ItemContext): Boolean {
        val handler = context.item[Capabilities.EnergyStorage.ITEM] ?: return false
        val toConsume = stack.energyConsumeAmount
        if (handler.extractEnergy(toConsume, true) < toConsume) return false
        handler.extractEnergy(toConsume, false)
        this += 1
        return true
    }
}