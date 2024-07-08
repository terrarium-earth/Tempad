package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.client.widgets.HorizontalListWidget
import earth.terrarium.tempad.client.widgets.TimelineEntry
import earth.terrarium.tempad.client.widgets.buttons.ColorChoice
import earth.terrarium.tempad.common.data.HistoricalLocation
import earth.terrarium.tempad.common.network.c2s.BackTrackLocation
import earth.terrarium.tempad.common.utils.sendToServer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import java.util.Date

class TimeTwisterScreen(val locations: Map<Date, HistoricalLocation>): Screen(CommonComponents.EMPTY) {
    companion object {
        val SPRITE = "screen/time_twister".tempadId
    }

    var leftPos: Int = 0
    var topPos: Int = 0

    override fun init() {
        super.init()

        leftPos = (width - 182) / 2
        topPos = height - 90

        val testButtons = HorizontalListWidget(164, 46)
        testButtons.setPosition(leftPos + 9, topPos + 1)

        for ((id, location) in locations) {
            testButtons.add(TimelineEntry(font, location) {
                BackTrackLocation(id).sendToServer()
                onClose()
            })
        }

        testButtons.scrollToBottom()

        addRenderableWidget(testButtons)
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, pPartialTick: Float) {
        super.renderBackground(graphics, mouseX, mouseY, pPartialTick)
        graphics.blitSprite(SPRITE, width / 2 - 91, height - 90, 182, 48)
    }
}