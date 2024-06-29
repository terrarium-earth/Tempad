package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

abstract class AbstractTempadScreen<T: AbstractTempadMenu>(menu: T, inv: Inventory, title: Component): AbstractContainerScreen<T>(menu, inv, title) {

}