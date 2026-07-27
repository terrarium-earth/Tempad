package earth.terrarium.tempad.common.menu

import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

abstract  class AbstractTemputerMenu(menuType: MenuType<*>?, containerId: Int) : AbstractContainerMenu(menuType, containerId) {
    override fun quickMoveStack(
        var1: Player,
        var2: Int,
    ): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(var1: Player): Boolean = true
}