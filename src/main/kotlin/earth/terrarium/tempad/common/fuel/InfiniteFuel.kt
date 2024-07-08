package earth.terrarium.tempad.common.fuel

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.FuelHandler
import earth.terrarium.tempad.api.fuel.ItemContext
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

object InfiniteFuel: FuelHandler {
    override val id: ResourceLocation = "infinite".tempadId
    override val charges: Int = 1
    override val totalCharges: Int = 1
    override val hasCharges: Boolean = true
    override val hasSpaceLeft: Boolean = false

    override fun minusAssign(amount: Int) {}
}