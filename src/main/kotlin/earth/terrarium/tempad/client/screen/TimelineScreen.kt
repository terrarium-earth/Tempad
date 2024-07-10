package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.client.widgets.HorizontalListWidget
import earth.terrarium.tempad.client.widgets.TimelineEntry
import earth.terrarium.tempad.common.network.c2s.BackTrackLocation
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.sendToServer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class TimelineScreen(menu: ModMenus.TimelineMenu, inv: Inventory, title: Component): AbstractTempadScreen<ModMenus.TimelineMenu>(null, menu, inv, title) {
    override fun init() {
        super.init()

        val testButtons = HorizontalListWidget(198, 118)
        testButtons.setPosition(localLeft, localTop)

        for ((time, location) in menu.appContent!!.history.toSortedMap()) {
            testButtons.add(TimelineEntry(font, location) {
                BackTrackLocation(time).sendToServer()
                onClose()
            })
        }

        testButtons.scrollToBottom()
        addRenderableWidget(testButtons)
    }
}