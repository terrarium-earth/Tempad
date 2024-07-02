package earth.terrarium.tempad.client.widgets.buttons

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.CommonComponents

class ToggleButton(
    private val selectedSprites: WidgetSprites,
    private val unselectedSprites: WidgetSprites,
    onPress: (btn: Button) -> Unit,
) :
    Button(0, 0, 14, 14, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION) {
    var toggled = false
    var tooltip = { selected: Boolean -> Tooltip.create(CommonComponents.EMPTY) }

    public override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val sprites = if (this.toggled) this.selectedSprites else this.unselectedSprites
        val resourceLocation = sprites[this.isActive, isHovered()]
        graphics.blitSprite(resourceLocation, this.x, this.y, this.width, this.height)
    }

    override fun onPress() {
        this.toggled = !this.toggled
        this.setTooltip(this.tooltip(toggled))
        super.onPress()
    }
}
