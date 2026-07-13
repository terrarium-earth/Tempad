package earth.terrarium.tempad.client.widgets

import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.renderer.RenderPipelines

class UnclickableWidget: BaseWidget() {
    var renderer: WidgetRenderer<in AbstractWidget> = WidgetRenderer.empty<AbstractWidget>()
    var sprites: WidgetSprites? = null

    override fun extractWidgetRenderState(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        sprites?.let {
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
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