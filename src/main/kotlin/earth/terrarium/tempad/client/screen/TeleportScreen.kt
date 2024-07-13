package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.client.widgets.InformationPanel
import earth.terrarium.tempad.client.widgets.ModWidgets
import earth.terrarium.tempad.client.widgets.buttons.ToggleButton
import earth.terrarium.tempad.client.widgets.colored.ColoredButton
import earth.terrarium.tempad.client.widgets.location_panel.PanelWidget
import earth.terrarium.tempad.common.data.FavoriteLocationAttachment
import earth.terrarium.tempad.common.network.c2s.DeleteLocationPacket
import earth.terrarium.tempad.common.network.c2s.OpenTimedoorPacket
import earth.terrarium.tempad.common.network.c2s.SetFavoritePacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.btnSprites
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.lwjgl.glfw.GLFW
import java.util.*

class TeleportScreen(menu: ModMenus.TeleportMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.TeleportMenu>(SPRITE, menu, inv, title) {
    companion object {
        val SPRITE = "screen/teleport".tempadId

        fun imgBtn(key: String, onClick: (btn: Button) -> Unit): ImageButton {
            val imageButton = ImageButton(10, 10, key.btnSprites(), onClick, key.toLanguageKey("button"))
            imageButton.tooltip = Tooltip.create(key.toLanguageKey("button"))
            return imageButton
        }

        val teleportText = Component.translatable("app.${Tempad.MOD_ID}.teleport.button")
    }

    var selected: Triple<ProviderSettings, UUID, LocationData>? = null
        set(value) {
            field = value
            locationButtons.visitWidgets { widget -> widget.visible = value != null }
            favBtn.toggled = favorite?.matches(value?.first?.id, value?.second) == true
            value?.third?.let { infoPanel.update(it) }
        }

    private lateinit var locationButtons: LinearLayout
    private lateinit var infoPanel: InformationPanel
    private lateinit var favBtn: ToggleButton
    private lateinit var search: EditBox
    private var favorite: FavoriteLocationAttachment? = menu.appContent?.favoriteLocation
    private lateinit var locationList: PanelWidget

    override fun init() {
        super.init()
        infoPanel = addRenderableWidget(InformationPanel())
        infoPanel.setPosition(localLeft + 4, localTop + 23)

        val searchValue = if (::search.isInitialized) search.value else ""

        this.locationList = addRenderableWidget(PanelWidget(
            menu.appContent?.locations ?: emptyMap(),
            this::selected,
            { selected = it },
            { search.value },
            { provider, locationId -> favorite?.matches(provider.id, locationId) ?: false }
        ))

        locationList.setPosition(localLeft + 100, localTop + 23)

        this.search = addRenderableWidget(
            ModWidgets.search(
                localLeft + 115,
                localTop + 8,
                64,
                12
            ) { _ -> locationList.update() }
        )

        search.value = searchValue
        locationList.update()

        locationButtons = LinearLayout(
            localLeft + 71,
            localTop + 26,
            LinearLayout.Orientation.HORIZONTAL
        ).spacing(2)

        addRenderableWidget(
            ColoredButton(teleportText, height = 18, width = 74, x = localLeft + 4, y = localTop + 96) {
                if (minecraft == null || selected == null) return@ColoredButton
                val (provider, locationId, _) = selected!!
                OpenTimedoorPacket(provider.id, locationId, menu.ctx).sendToServer()
                minecraft?.setScreen(null)
            },
        ).active = false

        favBtn = locationButtons.addChild(
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
        )

        favBtn.tooltip = { selected ->
            Tooltip.create(Component.translatable(
                if (selected) "gui.${Tempad.MOD_ID}.unfavorite.button" else "gui.${Tempad.MOD_ID}.favorite.button"
            ))
        }

        favBtn.setTooltip(Tooltip.create(Component.translatable("gui.${Tempad.MOD_ID}.favorite.button")))

        locationButtons.addChild(
            imgBtn("delete") {
                if (minecraft == null || selected == null) return@imgBtn
                val (provider, locationId, _) = selected!!
                DeleteLocationPacket(menu.ctx, provider.id, locationId).sendToServer()
                locationList.deleteSelected()
                infoPanel.clearLines()
            },
        )

        locationButtons.arrangeElements()
        locationButtons.visitWidgets { widget ->
            addRenderableWidget(widget)
            widget.visible = false
        }
    }

    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        if (locationList.isScrolling) {
            locationList.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        }
        return true
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (!search.isMouseOver(pMouseX, pMouseY)) {
            search.isFocused = false
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton)
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if (search.isFocused) {
            if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
                search.isFocused = false
                return true
            }
            return search.keyPressed(pKeyCode, pScanCode, pModifiers)
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }
}