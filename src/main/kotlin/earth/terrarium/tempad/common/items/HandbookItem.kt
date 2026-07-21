package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.client.TempadClient
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class HandbookItem(props: Properties): Item(props) {
    override fun use(
        level: Level,
        player: Player,
        usedHand: InteractionHand,
    ): InteractionResult {
        if(level.isClientSide) TempadClient.openGuide()
        return InteractionResult.SUCCESS
    }
}