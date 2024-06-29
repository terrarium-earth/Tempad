package earth.terrarium.tempad.api.fuel

import earth.terrarium.tempad.Tempad.Companion.tempadId
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability

interface FuelConsumer {
    companion object {
        val ITEM_CAP = ItemCapability.create("fuel".tempadId, FuelConsumer::class.java, Void::class.java)
        val BLOCK_CAP = BlockCapability.create("fuel".tempadId, FuelConsumer::class.java, Void::class.java)
    }

    val fuelAmount: Int
    val capacity: Int

    fun consumeFuel()
    fun canConsumeFuel(): Boolean
}