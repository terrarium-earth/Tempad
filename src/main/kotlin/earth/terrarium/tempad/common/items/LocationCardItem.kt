package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.common.registries.targetPos
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
        if (!level.isClientSide) {
            player.getItemInHand(usedHand).targetPos?.let { pos ->
                // player.displayClientMessage(pos.consume(player), true) TODO: Implement this
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return (stack.targetPos as? TooltipComponent)?.let { pos -> Optional.of(pos) } ?: Optional.empty()
    }
}