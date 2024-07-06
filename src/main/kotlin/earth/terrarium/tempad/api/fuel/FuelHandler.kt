package earth.terrarium.tempad.api.fuel

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.utils.get
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.capabilities.ItemCapability

interface FuelHandler {
    companion object {
        @JvmField
        val CAPABILITY = ItemCapability.create("fuel".tempadId, FuelHandler::class.java, Void::class.java)

        fun moveCharge(from: ItemStack, to: ItemStack): Boolean {
            val fromFuelHandler = from[CAPABILITY] ?: return false
            val toFuelHandler = to[CAPABILITY] ?: return false
            if (!fromFuelHandler.hasCharges || !toFuelHandler.hasSpaceLeft) return false
            fromFuelHandler.consumeCharge()
            toFuelHandler.addCharge()
            return true
        }
    }

    val charges: Int
    val totalCharges: Int

    @Suppress("INAPPLICABLE_JVM_NAME")
    val hasCharges: Boolean
        @JvmName("hasCharges") get() = charges > 0

    @Suppress("INAPPLICABLE_JVM_NAME")
    val hasSpaceLeft: Boolean
        @JvmName("hasSpaceLeft") get() = charges < totalCharges

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("consumeCharges")
    operator fun minusAssign(amount: Int)

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("addCharges")
    operator fun plusAssign(amount: Int)

    fun consumeCharge() {
        this -= 1
    }

    fun addCharge() {
        this += 1
    }
}