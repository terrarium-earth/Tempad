package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.client.widgets.InformationPanel
import earth.terrarium.tempad.client.widgets.LocationPanel
import earth.terrarium.tempad.client.widgets.ModWidgets
import earth.terrarium.tempad.client.widgets.ToggleButton
import earth.terrarium.tempad.common.network.c2s.DeleteLocationPacket
import earth.terrarium.tempad.common.network.c2s.OpenTimedoorPacket
import earth.terrarium.tempad.common.network.c2s.SetFavoritePacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.btnSprites
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.Optionull
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import java.util.*

class TeleportScreen(menu: ModMenus.TeleportMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.TeleportMenu>(SPRITE, menu, inv, title) {
    companion object {
        val SPRITE = "screen/teleport".tempadId
        val CENTERED: (LayoutSettings) -> Unit = { it.alignHorizontallyRight() }

        fun imgBtn(key: String, onClick: (btn: Button) -> Unit): ImageButton {
            return ImageButton(14, 14, key.btnSprites(), onClick, key.toLanguageKey("button"))
        }
    }

    private var locationButtons: LinearLayout? = null
    private var selected: Triple<ProviderSettings, UUID, LocationData>? = null
    private var infoPanel: InformationPanel? = null
    private var locationPanel: LocationPanel? = null
    private var favBtn: ToggleButton? = null
    private var search: EditBox? = null

    override fun init() {
        super.init()
        infoPanel = InformationPanel(localLeft + 4, localTop + 4, 100, 100)

        locationPanel = LocationPanel(
            localLeft + 4, localTop + 4, 100, 100,
            menu.appContent?.locations ?: emptyMap(),
            { provider, locationId -> menu.appContent?.favoriteLocation?.matches(provider?.id, locationId) ?: false }
        ) { entry ->
            if (entry?.settings == null || entry.id == null || entry.data == null) return@LocationPanel
            selected = Triple(entry.settings, entry.id, entry.data)
            locationButtons?.let { it.visitWidgets { widget -> widget.active = true } }
            favBtn?.let {
                it.toggled = menu.appContent?.favoriteLocation?.matches(entry.settings.id, entry.id) == true
            }
        }

        val searchValue = Optionull.mapOrDefault(
            search,
            { obj: EditBox -> obj.value }, ""
        )

        this.search = addRenderableWidget(
            ModWidgets.search(
                localLeft + 113,
                localTop + 7,
                66,
                12
            ) { text -> locationPanel?.update(text) })

        search?.setValue(searchValue)

        locationButtons = LinearLayout(
            localLeft + 4,
            localTop + 100,
            LinearLayout.Orientation.HORIZONTAL
        ).spacing(2)

        val btns = locationButtons ?: return
        btns.addChild(
            imgBtn("teleport") {
                if (minecraft == null || selected == null) return@imgBtn
                val (provider, locationId, _) = selected!!
                OpenTimedoorPacket(provider.id, locationId, 0).sendToServer()
                minecraft?.setScreen(null)
            },
            CENTERED
        )

        favBtn = btns.addChild(
            ToggleButton("favorite".btnSprites(), "unfavorite".btnSprites()) {
                if (minecraft == null || selected == null) return@ToggleButton
                val (provider, locationId, _) = selected!!
                if (menu.appContent?.favoriteLocation?.matches(provider.id, locationId) == true) {
                    SetFavoritePacket(null).sendToServer()
                } else {
                    SetFavoritePacket(provider.id, locationId).sendToServer()
                }
            },
            CENTERED
        )

        btns.addChild(
            imgBtn("delete") {
                if (minecraft == null || selected == null) return@imgBtn
                val (provider, locationId, _) = selected!!
                DeleteLocationPacket(provider.id, locationId).sendToServer()
                locationPanel?.update(this.search?.value ?: "")
            },
            CENTERED
        )

        btns.addChild(
            imgBtn("download") {
                selected = null
                locationButtons?.let { it.visitWidgets { widget -> widget.active = false } }
                minecraft?.setScreen(null)
            },
            CENTERED
        )

        btns.visitWidgets { widget -> widget.active = false }
    }
}