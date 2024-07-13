package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.FuelHandler
import earth.terrarium.tempad.client.widgets.colored.ColoredButton
import earth.terrarium.tempad.common.apps.AppContent
import earth.terrarium.tempad.common.fuel.EmptyFuel
import earth.terrarium.tempad.common.menu.FuelMenu
import earth.terrarium.tempad.common.network.c2s.AddFuelPacket
import earth.terrarium.tempad.common.network.c2s.TransferFuelPacket
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.sendToServer
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.LinearLayout
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

        val AUTO_USE_TEXT = Component.translatable("app.tempad.fuel.auto_use")
        val AUTO_USE_TOOLTIP = Component.translatable("app.tempad.fuel.auto_use.description")
        val USE_TEXT = Component.translatable("app.tempad.fuel.use")
        val USE_TOOLTIP = Component.translatable("app.tempad.fuel.use.description")
        val TRANSFER_TEXT = Component.translatable("app.tempad.fuel.transfer")
        val TRANSFER_TOOLTIP = Component.translatable("app.tempad.fuel.transfer.description")
    }

    val fuelHandler = menu.stack[FuelHandler.CAPABILITY] ?: EmptyFuel

    override fun init() {
        super.init()

        val infoLayout = LinearLayout(86, 86, LinearLayout.Orientation.VERTICAL)
        infoLayout.setPosition(localLeft + 9, localTop + 25)
        infoLayout.spacing(2)

        infoLayout.addChild(
            StringWidget(
                Component.translatable(fuelHandler.id.toLanguageKey("fuel")),
                minecraft!!.font
            )
        ).setColor(Tempad.ORANGE.value)

        infoLayout.addChild(
            MultiLineTextWidget(
                Component.translatable(fuelHandler.id.toLanguageKey("fuel") + ".description"),
                minecraft!!.font
            )
        ).setMaxWidth(90).setColor(Tempad.ORANGE.value)

        infoLayout.arrangeElements()
        infoLayout.visitWidgets { addRenderableWidget(it) }

        addRenderableWidget(ColoredButton(AUTO_USE_TEXT, width = 65, height = 18, x = localLeft + 101, y = localTop + 30) {
            AddFuelPacket(menu.ctx, true).sendToServer()
        }).tooltip = Tooltip.create(AUTO_USE_TOOLTIP)

        addRenderableWidget(ColoredButton(USE_TEXT, width = 45, height = 18, x = localLeft + 101, y = localTop + 50) {
            AddFuelPacket(menu.ctx, false).sendToServer()
        }).tooltip = Tooltip.create(USE_TOOLTIP)

        addRenderableWidget(ColoredButton(TRANSFER_TEXT, width = 45, height = 18, x = localLeft + 101, y = localTop + 70) {
            TransferFuelPacket(menu.ctx).sendToServer()
        }).tooltip = Tooltip.create(TRANSFER_TOOLTIP)
    }
}