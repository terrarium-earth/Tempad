package earth.terrarium.tempad.client.widgets

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack
import net.minecraft.client.gui.GuiGraphics

abstract class KListEntry: ListEntry() {
    abstract var focused: Boolean

    override fun setFocused(pFocused: Boolean) {
        focused = pFocused
    }

    override fun isFocused(): Boolean = focused
}