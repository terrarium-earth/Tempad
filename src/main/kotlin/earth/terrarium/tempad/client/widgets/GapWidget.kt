package earth.terrarium.tempad.client.widgets

import net.minecraft.client.gui.GuiGraphics

class GapWidget(override var _height: Int) : KListWidgetItem {
    override var _x: Int = 0
    override var _y: Int = 0
    override var _focused: Boolean = false
    override var _width: Int = 0

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {}
}