package earth.terrarium.tempad.common.apps

import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.menu.FuelMenu
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

class FuelApp(val slotId: Int): TempadApp<BasicAppContent> {
    override fun isEnabled(player: Player): Boolean = true

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return FuelMenu(pContainerId, pPlayerInventory, null)
    }

    override fun getDisplayName(): Component = Component.translatable("menu.tempad.fuel")

    override fun createContent(player: ServerPlayer?): BasicAppContent = BasicAppContent(slotId)
}