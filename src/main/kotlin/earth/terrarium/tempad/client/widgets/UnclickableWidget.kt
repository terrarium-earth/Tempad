package earth.terrarium.tempad.client.widgets

import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites

class UnclickableWidget: BaseWidget() {
    var renderer: WidgetRenderer<in AbstractWidget> = WidgetRenderer.empty<AbstractWidget>()
    var sprites: WidgetSprites? = null

    override fun renderWidget(graphics: GuiGraphics, x: Int, y: Int, partialTick: Float) {
        sprites?.let {
            graphics.blitSprite(
                it.get(this.active, false),
                this.x,
                this.y,
                this.getWidth(),
                this.getHeight()
            )
        }

        this.renderer.render(graphics, WidgetRendererContext(this, x, y), partialTick)
    }
}