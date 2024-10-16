package earth.terrarium.tempad.client

import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.dropdown.DropdownBuilder
import earth.terrarium.olympus.client.components.dropdown.DropdownState
import earth.terrarium.olympus.client.components.renderers.ColorableWidget
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.utils.State
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.Component

object TempadUI {
    val button = WidgetSprites("button/normal".tempadId, "button/disabled".tempadId, "button/hover".tempadId)
    val element = WidgetSprites("element/normal".tempadId, "element/disabled".tempadId, "element/hover".tempadId)
    val modal = "list/modal".tempadId

    fun <T: AbstractWidget, W> W.colored(): WidgetRenderer<T> where W: WidgetRenderer<T>, W: ColorableWidget  {
        return WidgetRenderers.withColors(this, Tempad.DARK_ORANGE, Tempad.ORANGE, Tempad.HIGHLIGHTED_ORANGE)
    }

    fun <T: AbstractWidget, W> W.selectableColored(selected: () -> Boolean): WidgetRenderer<T> where W: WidgetRenderer<T>, W: ColorableWidget {
        return WidgetRenderer { graphics, ctx, partialTick ->
            if (selected()) {
                this.withColor(MinecraftColors.BLACK)
                this.render(graphics, ctx, partialTick)
            } else {
                WidgetRenderers.withColors(this, Tempad.DARK_ORANGE, Tempad.ORANGE, Tempad.HIGHLIGHTED_ORANGE).render(graphics, ctx, partialTick)
            }
        }
    }

    fun <T: AbstractWidget> selectionBg(selected: () -> Boolean): WidgetRenderer<T> {
        return WidgetRenderer { graphics, ctx, partialTick ->
            if (selected()) {
                WidgetRenderers.solid<T>().withColor(Tempad.ORANGE).render(graphics, ctx, partialTick)
            }
        }
    }

    fun <T> DropdownBuilder<T>.style(converter: (T) -> Component) {
        this.withTexture(modal)
        this.withEntryHeight(14)
        this.withEntrySprites(null)
        this.withEntryRenderer { entry ->
            WidgetRenderers.text<Button>(converter(entry)).withLeftAlignment().colored().withPadding(0, 3)
        }
    }
}