package earth.terrarium.tempad.common.menu

import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.common.apps.AppContent
import earth.terrarium.tempad.common.apps.BasicAppContent
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.jvm.optionals.getOrNull

class FuelMenu(id: Int, inventory: Inventory, appContent: BasicAppContent?): AbstractTempadMenu<BasicAppContent>(id, inventory, ModMenus.FUEL_MENU, appContent) {
    val container = SimpleContainer(2)

    constructor(id: Int, inventory: Inventory, locations: Optional<BasicAppContent>) : this(id, inventory, locations.getOrNull())

    init {
        container.addListener { this.slotsChanged(this.container) }
        addSlot(Slot(container, 0, 179, 60))
        addSlot(Slot(container, 1, 179, 80))
    }

    override fun removed(pPlayer: Player) {
        super.removed(pPlayer)
        container.removeAllItems().forEach { putBack(pPlayer, it) }
    }

    private fun putBack(player: Player, stack: ItemStack) {
        if (player is ServerPlayer) {
            if (player.isAlive && !player.hasDisconnected()) {
                player.inventory.placeItemBackInInventory(stack)
            } else {
                player.drop(stack, false)
            }
        }
    }
}