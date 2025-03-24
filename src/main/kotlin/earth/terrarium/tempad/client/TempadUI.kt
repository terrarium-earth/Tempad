package earth.terrarium.tempad.client

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.dropdown.DropdownBuilder
import earth.terrarium.olympus.client.components.renderers.ColorableWidget
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.components.textbox.TextBox
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.utils.State
import earth.terrarium.olympus.client.utils.StateUtils
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.network.chat.Component
import org.apache.commons.lang3.math.NumberUtils.toFloat
import org.openjdk.nashorn.internal.runtime.JSType.toDouble
import java.util.function.Consumer

object TempadUI {
    val button = WidgetSprites("button/normal".tempadId, "button/disabled".tempadId, "button/hover".tempadId)
    val element = WidgetSprites("element/normal".tempadId, "element/disabled".tempadId, "element/hover".tempadId)
    val modal = "list/modal".tempadId

    val toggleEnabled = WidgetSprites("toggle/enabled/normal".tempadId, "toggle/enabled/disabled".tempadId, "toggle/enabled/hover".tempadId)
    val toggleDisabled = WidgetSprites("toggle/disabled/normal".tempadId, "toggle/disabled/disabled".tempadId, "toggle/disabled/hover".tempadId)

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

    fun toggle(state: State<Boolean>): Button {
        val onRenderer = WidgetRenderers.sprite<Button?>(toggleEnabled)
        val offRenderer = WidgetRenderers.sprite<Button?>(toggleDisabled)
        val switchFactory = Consumer { button: Button? ->
            button!!.withCallback(StateUtils.booleanToggle(state)).withTexture(null as WidgetSprites?)
                .withRenderer(WidgetRenderer { graphics: GuiGraphics?, context: WidgetRendererContext<Button?>?, partialTick: Float ->
                    (if (state.get()) onRenderer else offRenderer).render(
                        graphics,
                        context,
                        partialTick
                    )
                })
        }
        return Widgets.button(switchFactory)
    }

    fun intInput(state: State<Int>, modify: (TextBox) -> Unit): TextBox {
        return Widgets.intInput(state) {
            it.withTexture(element)
            it.withTextColor(Tempad.ORANGE)
            modify(it)
        }
    }

    fun floatInput(state: State<Float>, modify: (TextBox) -> Unit): TextBox {
        return Widgets.doubleInput(state.map(Float::toDouble, Double::toFloat)) {
            it.withTexture(element)
            it.withTextColor(Tempad.ORANGE)
            modify(it)
        }
    }
}