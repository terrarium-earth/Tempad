package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.locations.DirectLocation
import earth.terrarium.tempad.common.registries.portalTarget
import net.minecraft.network.chat.Component
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
            player.getItemInHand(usedHand).portalTarget?.let { pos ->
                if (pos is DirectLocation) {
                    player.displayClientMessage(Component.translatable("item.tempad.location_card.added_location", pos.location.name), true)
                } else {
                    player.displayClientMessage(Component.translatable("item.tempad.location_card.dynamic_error"), true)
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.ofNullable(stack.portalTarget as? TooltipComponent)
    }
}