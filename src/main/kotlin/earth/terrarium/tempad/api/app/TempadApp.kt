package earth.terrarium.tempad.api.app

import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface TempadApp<T: MenuContent<T>>: ContentMenuProvider<T> {
    fun isAppAvailable(player: Player, stack: ItemStack): Boolean
}
