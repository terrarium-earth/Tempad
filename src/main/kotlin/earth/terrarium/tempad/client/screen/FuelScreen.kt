package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.client.widgets.buttons.ButtonType
import earth.terrarium.tempad.client.widgets.buttons.FlatButton
import earth.terrarium.tempad.common.apps.AppContent
import earth.terrarium.tempad.common.menu.FuelMenu
import earth.terrarium.tempad.common.network.c2s.AddFuelPacket
import earth.terrarium.tempad.common.network.c2s.TransferFuelPacket
import earth.terrarium.tempad.common.utils.sendToServer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class FuelScreen<T: AppContent<T>>(
    menu: FuelMenu, inv: Inventory,
    title: Component
) : AbstractTempadScreen<FuelMenu>(
    SPRITE, menu, inv, title
) {
    companion object {
        val SPRITE = "screen/fuel".tempadId

        val ADD_TEXT = Component.translatable("menu.tempad.fuel.add")
        val TRANSFER_TEXT = Component.translatable("menu.tempad.fuel.transfer")
    }

    override fun init() {
        super.init()

        addRenderableWidget(FlatButton(ADD_TEXT, type = ButtonType.FANCY, width = 46, height = 18, x = localLeft + 101, y = localTop + 75) {
            AddFuelPacket(menu.appContent!!.slotId).sendToServer()
        })

        addRenderableWidget(FlatButton(TRANSFER_TEXT, type = ButtonType.FANCY, width = 46, height = 18, x = localLeft + 101, y = localTop + 95) {
            TransferFuelPacket(menu.appContent!!.slotId).sendToServer()
        })
    }
}