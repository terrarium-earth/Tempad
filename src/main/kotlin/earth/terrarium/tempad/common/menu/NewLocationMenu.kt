package earth.terrarium.tempad.common.menu

import earth.terrarium.tempad.common.apps.AllowLocationSettingData
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.world.entity.player.Inventory
import java.util.*
import kotlin.jvm.optionals.getOrNull

class NewLocationMenu(val id: Int, val inventory: Inventory, val locations: AllowLocationSettingData?): AbstractTempadMenu(id, inventory, ModMenus.TELEPORT_MENU) {
    constructor(id: Int, inventory: Inventory, locations: Optional<AllowLocationSettingData>): this(id, inventory, locations.getOrNull())

    override fun addSlots() {}
}
