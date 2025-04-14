package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.client.TempadClient
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class HandbookItem: Item(Properties()) {
    override fun use(
        level: Level,
        player: Player,
        usedHand: InteractionHand,
    ): InteractionResultHolder<ItemStack?> {
        if(level.isClientSide) TempadClient.openGuide()
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }
}