package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.client.tooltip.ChrononData
import earth.terrarium.tempad.client.tooltip.tooltip
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*

abstract class ChrononItem: Item(Properties().stacksTo(1)) {
    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.of(stack.chronons!!.tooltip)
    }
}