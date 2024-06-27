package earth.terrarium.tempad.common.menu

import earth.terrarium.tempad.common.apps.SettingsData
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.world.entity.player.Inventory
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

data class TempadSettingsMenu(val id: Int, val inventory: Inventory, val settingsData: SettingsData?) : AbstractTempadMenu(id, inventory, ModMenus.SETTINGS_MENU) {
    constructor(id: Int, inventory: Inventory, settingsData: Optional<SettingsData>): this(id, inventory, settingsData.getOrNull())

    override fun addSlots() {}
}
