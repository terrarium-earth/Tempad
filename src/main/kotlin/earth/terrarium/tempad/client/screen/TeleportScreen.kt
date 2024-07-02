package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.client.widgets.InformationPanel
import earth.terrarium.tempad.client.widgets.ModWidgets
import earth.terrarium.tempad.client.widgets.buttons.ToggleButton
import earth.terrarium.tempad.client.widgets.location_panel.PanelWidget
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
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
import net.minecraft.client.gui.components.Tooltip
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
            val imageButton = ImageButton(14, 14, key.btnSprites(), onClick, key.toLanguageKey("button"))
            imageButton.tooltip = Tooltip.create(key.toLanguageKey("button"))
            return imageButton
        }
    }

    private var locationButtons: LinearLayout? = null
    var selected: Triple<ProviderSettings, UUID, LocationData>? = null
        set(value) {
            field = value
            locationButtons?.visitWidgets { widget -> widget.active = value != null }
            favBtn?.toggled = favorite?.matches(value?.first?.id, value?.second) == true
            value?.third?.let { infoPanel?.update(it) }
        }
    private var infoPanel: InformationPanel? = null
    private var favBtn: ToggleButton? = null
    private var search: EditBox? = null
    private var favorite: FavoriteLocationAttachment? = menu.appContent?.favoriteLocation
    private var panel: PanelWidget? = null

    override fun init() {
        super.init()
        infoPanel = addRenderableWidget(InformationPanel(localLeft + 4, localTop + 4))
        infoPanel!!.setPosition(localLeft + 4, localTop + 21)

        panel = addRenderableWidget(PanelWidget(
            menu.appContent?.locations ?: emptyMap(),
            this::selected,
            { selected = it },
            { search?.value ?: "" },
            { provider, locationId -> favorite?.matches(provider.id, locationId) ?: false }
        ))

        panel?.update()
        panel?.setPosition(localLeft + 101, localTop + 21)

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
            ) { text -> panel?.update() })

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
            ToggleButton("unfavorite".btnSprites(), "favorite".btnSprites()) {
                if (minecraft == null || selected == null) return@ToggleButton
                val (provider, locationId, _) = selected!!
                if (favorite?.matches(provider.id, locationId) == true) {
                    SetFavoritePacket(null).sendToServer()
                    favorite = null
                } else {
                    SetFavoritePacket(provider.id, locationId).sendToServer()
                    favorite = FavoriteLocationAttachment(provider.id, locationId)
                }
            },
            CENTERED
        )

        favBtn?.tooltip = { selected ->
            Tooltip.create(Component.translatable(
                if (selected) "gui.${Tempad.MOD_ID}.unfavorite.button" else "gui.${Tempad.MOD_ID}.favorite.button"
            ))
        }

        favBtn?.setTooltip(Tooltip.create(Component.translatable("gui.${Tempad.MOD_ID}.favorite.button")))

        btns.addChild(
            imgBtn("delete") {
                if (minecraft == null || selected == null) return@imgBtn
                val (provider, locationId, _) = selected!!
                DeleteLocationPacket(provider.id, locationId).sendToServer()
                panel?.deleteSelected()
                infoPanel?.clearLines()
            },
            CENTERED
        )

        btns.addChild(
            imgBtn("download") {
                locationButtons?.let { it.visitWidgets { widget -> widget.active = false } }
                minecraft?.setScreen(null)
            },
            CENTERED
        )

        btns.arrangeElements()
        btns.visitWidgets { widget ->
            addRenderableWidget(widget)
            widget.active = false
        }
    }

    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        if (panel?.isScrolling == true) {
            panel?.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        }
        return true
    }
}