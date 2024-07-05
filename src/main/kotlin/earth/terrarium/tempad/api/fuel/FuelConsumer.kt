package earth.terrarium.tempad.api.fuel

import earth.terrarium.tempad.Tempad.Companion.tempadId
import net.neoforged.neoforge.capabilities.ItemCapability

interface FuelConsumer {
    companion object {
        @JvmField
        val CAPABILITY = ItemCapability.create("fuel".tempadId, FuelConsumer::class.java, Void::class.java)
    }

    val charges: Int
    val totalCharges: Int

    @Suppress("INAPPLICABLE_JVM_NAME")
    val hasCharges: Boolean
        @JvmName("hasCharges") get() = charges > 0

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("consumeCharges")
    operator fun minusAssign(amount: Int)

    fun consumeCharge() {
        this -= 1
    }
}