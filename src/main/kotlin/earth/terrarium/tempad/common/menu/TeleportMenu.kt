package earth.terrarium.tempad.common.menu

import earth.terrarium.tempad.common.apps.TeleportData
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import java.util.*

class TeleportMenu(id: Int, inventory: Inventory, appContent: TeleportData) :
    AbstractTempadMenu<TeleportData>(id, inventory, ModMenus.TELEPORT_MENU, appContent) {

    val container = SimpleContainer(1)

    constructor(id: Int, inventory: Inventory, data: Optional<TeleportData>) : this(id, inventory, data.orElseThrow())

    init {
        addSlot(Slot(container, 0, 111, 117))
    }

    override fun removed(pPlayer: Player) {
        super.removed(pPlayer)
        container.removeAllItems().forEach { putBack(pPlayer, it) }
    }
}