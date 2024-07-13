package earth.terrarium.tempad.api.context

import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot

class ContextInstance(val ctx: ItemContext, val player: Player) {
    var stack by ctx.stackDelegate(player)
    val level = player.level()

    fun isLocked(slot: Slot) = ctx.isLocked(slot, player)
}