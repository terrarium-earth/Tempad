package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.client.widgets.InformationPanel
import earth.terrarium.tempad.client.widgets.MapWidget
import earth.terrarium.tempad.common.network.c2s.CreateLocationPacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.btnSprites
import earth.terrarium.tempad.common.utils.globalPos
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class NewLocationScreen(menu: ModMenus.NewLocationMenu, inv: Inventory, title: Component): AbstractTempadScreen<ModMenus.NewLocationMenu>(SPRITE, menu, inv, title) {
    companion object {
        val SPRITE = "screen/new_location".tempadId
        val NAME_FIELD = "new_location.name".toLanguageKey("screen")
    }

    override fun init() {
        super.init()

        addRenderableOnly(MapWidget(localLeft + 101, localTop + 21, 92, 5))

        val informationPanel = addRenderableWidget(
            InformationPanel(
                this.localLeft + 6,
                this.localTop + 24, 91, 78
            )
        )

        minecraft?.player?.globalPos?.let { informationPanel.updateNameless(it) }

        val box = addRenderableWidget(
            EditBox(
                this.font,
                this.localLeft + 8,
                this.localTop + 103, 74, 8, CommonComponents.EMPTY
            )
        )
        box.setMaxLength(32)
        box.isBordered = false
        box.setHint(NAME_FIELD)
        box.setTextColor(Tempad.ORANGE.value)

        addRenderableWidget(ImageButton(
            this.localLeft + 84, this.localTop + 100, 14, 14,
            "save".btnSprites()
        ) {
            CreateLocationPacket(box.value, Color.DEFAULT)
        }).setTooltip(Tooltip.create(CommonComponents.GUI_DONE))
    }
}