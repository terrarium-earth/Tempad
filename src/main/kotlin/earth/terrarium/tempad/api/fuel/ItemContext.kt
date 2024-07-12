package earth.terrarium.tempad.api.fuel

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

interface ItemContext {
    var stack: ItemStack
    val player: Player
    val slot: Int
    val level: Level
}