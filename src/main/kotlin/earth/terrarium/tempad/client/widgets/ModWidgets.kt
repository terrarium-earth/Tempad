package earth.terrarium.tempad.client.widgets

import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.utils.sprites
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import java.util.function.Consumer

object ModWidgets {
    val CHEVRON_UP = "chevron_up".sprites("misc")
    val CHEVRON_DOWN = "chevron_down".sprites("misc")

    private val SEARCH: Component = Component.translatable(("gui.${Tempad.MOD_ID}.search_field"))

    fun search(x: Int, y: Int, width: Int, height: Int, updater: (String) -> Unit): EditBox {
        val editBox = EditBox(Minecraft.getInstance().font, x, y, width, height, SEARCH)
        editBox.isBordered = false
        editBox.setHint(SEARCH)
        editBox.setTextColor(Tempad.ORANGE.value)
        editBox.setResponder(updater)
        return editBox
    }
}
