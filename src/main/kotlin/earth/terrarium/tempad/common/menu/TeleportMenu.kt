package earth.terrarium.tempad.common.menu

import earth.terrarium.tempad.common.apps.AvailableTeleportData
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

data class TeleportMenu(val id: Int, val inventory: Inventory, val locations: AvailableTeleportData?): AbstractTempadMenu(id, inventory, ModMenus.TELEPORT_MENU) {
    constructor(id: Int, inventory: Inventory, locations: Optional<AvailableTeleportData>): this(id, inventory, locations.getOrNull())

    override fun addSlots() {}
}
