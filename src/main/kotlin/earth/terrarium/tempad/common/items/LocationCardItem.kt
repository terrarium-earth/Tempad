package earth.terrarium.tempad.common.items

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.*

class LocationCardItem: Item(Properties()) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        return super.use(level, player, usedHand)
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return super.getTooltipImage(stack)
    }
}