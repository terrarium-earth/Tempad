package earth.terrarium.tempad.api.app

import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import earth.terrarium.tempad.common.apps.AppContent
import net.minecraft.world.entity.player.Player

interface TempadApp<T: AppContent<T>>: ContentMenuProvider<T> {
    fun isEnabled(player: Player): Boolean = true
}
