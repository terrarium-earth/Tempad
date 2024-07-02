package earth.terrarium.tempad.client.widgets.location_panel

import earth.terrarium.olympus.client.components.base.ListWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput
import java.util.function.Consumer

interface KListWidgetItem: ListWidget.Item {
    var _x: Int
    var _y: Int
    var _focused: Boolean
    var _width: Int
    var _height: Int

    override fun setFocused(pFocused: Boolean) {
        _focused = pFocused
    }

    override fun isFocused(): Boolean = _focused

    override fun updateNarration(pNarrationElementOutput: NarrationElementOutput) {}

    override fun narrationPriority() = NarratableEntry.NarrationPriority.NONE

    override fun setX(pX: Int) {
        _x = pX
    }

    override fun setY(pY: Int) {
        _y = pY
    }

    override fun getX(): Int = _x

    override fun getY(): Int = _y

    override fun getWidth(): Int = _width

    override fun getHeight(): Int = _height

    override fun visitWidgets(pConsumer: Consumer<AbstractWidget>) {

    }

    override fun setWidth(p0: Int) {
        _width = p0
    }
}