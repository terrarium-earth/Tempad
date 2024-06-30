package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.client.components.selection.SelectionList
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.fuel.FuelConsumer
import earth.terrarium.tempad.client.widgets.AppButton
import earth.terrarium.tempad.client.widgets.FuelBarWidget
import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import earth.terrarium.tempad.common.network.c2s.OpenAppPacket
import earth.terrarium.tempad.common.registries.ModNetworking
import earth.terrarium.tempad.common.utils.get
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

abstract class AbstractTempadScreen<T: AbstractTempadMenu<*>>(val appSprite: ResourceLocation, menu: T, val inv: Inventory, title: Component): AbstractContainerScreen<T>(menu, inv, title) {
    init {
        this.imageWidth = 256
        this.imageHeight = 256
    }

    var localLeft: Int = 0
    var localTop: Int = 0

    companion object {
        val SPRITE = "screen/tempad".tempadId
    }

    override fun init() {
        super.init()


        this.localTop = this.leftPos + 30
        this.localLeft = this.topPos + 20

        val appList = SelectionList<AppButton>(14, 21, 16, 116, 16) { button ->
            ModNetworking.CHANNEL.sendToServer(OpenAppPacket(button!!.app, menu.appContent?.slotId ?: -1))
        }

        for ((id, app) in AppRegistry.getAll(menu.appContent?.slotId ?: -1)) {
            appList.addEntry(AppButton(app, id))
        }

        val fuelConsumer = inv[menu.appContent?.slotId ?: -1].getCapability(FuelConsumer.CAPABILITY) ?: return

        addRenderableOnly(FuelBarWidget(fuelConsumer, 237, 52))
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick)
        graphics.blitSprite(SPRITE, this.leftPos, this.topPos, this.imageWidth, this.imageHeight)
        graphics.blitSprite(appSprite, this.leftPos + 30, this.topPos + 20, 198, 118)
    }
}