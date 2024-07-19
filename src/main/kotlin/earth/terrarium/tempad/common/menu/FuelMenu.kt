package earth.terrarium.tempad.common.menu

import earth.terrarium.tempad.common.apps.BasicAppContent
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.jvm.optionals.getOrNull

class FuelMenu(id: Int, inventory: Inventory, appContent: BasicAppContent): AbstractTempadMenu<BasicAppContent>(id, inventory, ModMenus.FUEL_MENU, appContent) {
    val container = SimpleContainer(2)

    constructor(id: Int, inventory: Inventory, locations: Optional<BasicAppContent>) : this(id, inventory, locations.orElseThrow())

    init {
        container.addListener { this.slotsChanged(this.container) }
        addSlot(Slot(container, 0, 179, 60))
        addSlot(Slot(container, 1, 179, 80))
    }

    override fun removed(pPlayer: Player) {
        super.removed(pPlayer)
        container.removeAllItems().forEach { putBack(pPlayer, it) }
    }
}