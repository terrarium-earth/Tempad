package earth.terrarium.tempad.client

import com.ibm.icu.text.NumberFormat
import com.teamresourceful.resourcefullibkt.client.scissor
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.compound.LayoutWidget
import earth.terrarium.olympus.client.components.dropdown.DropdownBuilder
import earth.terrarium.olympus.client.components.renderers.ColorableWidget
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.components.textbox.TextBox
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.ui.ClearableGridLayout
import earth.terrarium.olympus.client.utils.State
import earth.terrarium.olympus.client.utils.StateUtils
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.tempadId
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import java.util.function.Consumer
import kotlin.math.roundToInt

object TempadUI {
    val button = WidgetSprites("button/normal".tempadId, "button/disabled".tempadId, "button/hover".tempadId)
    val steelButton = WidgetSprites("button/steel/normal".tempadId, "button/steel/hover".tempadId)

    val element = WidgetSprites("element/normal".tempadId, "element/disabled".tempadId, "element/hover".tempadId)
    val modal = "list/modal".tempadId

    val toggleEnabled = WidgetSprites(
        "toggle/enabled/normal".tempadId,
        "toggle/enabled/disabled".tempadId,
        "toggle/enabled/hover".tempadId
    )
    val toggleDisabled = WidgetSprites(
        "toggle/disabled/normal".tempadId,
        "toggle/disabled/disabled".tempadId,
        "toggle/disabled/hover".tempadId
    )

    val powerBg = "power/background".tempadId
    val powerBar = "power/overlay".tempadId
    val powerVert = "power/overlay_vertical".tempadId

    val lockIcon = "icons/mini/lock".tempadId
    val unlockIcon = "icons/mini/unlock".tempadId
    val xIcon = "icons/mini/x".tempadId

    val scrollbarYRenderer: WidgetRenderer<LayoutWidget<ClearableGridLayout>> =
        WidgetRenderer { graphics, context, partialTick ->
            val widget = context.getWidget();
            val scrollHeight =
                ((context.height.toFloat() * (context.height.toFloat() / widget.contentHeight)).toInt() + (widget.viewHeight - context.height)).coerceIn(
                    0,
                    context.height
                )
            val scrollY =
                ((widget.yScroll.toFloat() + widget.overscrollY) / widget.contentHeight.toFloat() * context.height.toFloat()).toInt()
            graphics.fill(
                context.x - 2,
                context.y - 2,
                context.x + context.width + 2,
                context.y + widget.height - 2,
                0x4aff6f00.toInt()
            )
            graphics.fill(
                context.x + 2,
                context.y,
                context.x + context.width - 2,
                context.y + widget.height - 6,
                Tempad.ORANGE.value
            )
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                "button/normal".tempadId,
                context.x,
                context.y + scrollY,
                context.width,
                scrollHeight
            )
        }


    fun <T : AbstractWidget, W> W.colored(): WidgetRenderer<T> where W : WidgetRenderer<T>, W : ColorableWidget {
        return WidgetRenderers.withColors(this, Tempad.DARK_ORANGE, Tempad.ORANGE, Tempad.HIGHLIGHTED_ORANGE)
    }

    fun <T : AbstractWidget, W> W.selectableColored(selected: () -> Boolean): WidgetRenderer<T> where W : WidgetRenderer<T>, W : ColorableWidget {
        return WidgetRenderer { graphics, ctx, partialTick ->
            if (selected()) {
                this.withColor(MinecraftColors.BLACK)
                this.render(graphics, ctx, partialTick)
            } else {
                WidgetRenderers.withColors(this, Tempad.DARK_ORANGE, Tempad.ORANGE, Tempad.HIGHLIGHTED_ORANGE)
                    .render(graphics, ctx, partialTick)
            }
        }
    }

    fun <T : AbstractWidget> selectionBg(selected: () -> Boolean): WidgetRenderer<T> {
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
                .withRenderer(WidgetRenderer { graphics: GuiGraphicsExtractor?, context: WidgetRendererContext<Button?>?, partialTick: Float ->
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

    // borrowed from stack overflow cuz im tired https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java
    val c = arrayOf("kC", "mC", "bC", "tC")

    fun formatChronons(amount: Int): String {
        return coolFormat(amount.toDouble(), 0)
    }

    private fun coolFormat(n: Double, iteration: Int): String {
        if (iteration == 0 && n < 1000) return n.toInt().toString() + "C"
        val d = (n.toLong() / 100) / 10.0
        val isRound = (d * 10) % 10 == 0.0 //true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (if (d < 1000)  //this determines the class, i.e. 'k', 'm' etc
            ((if (d > 99.9 || isRound || (d > 9.99))  //this decides whether to trim the decimals
                d.toInt() * 10 / 10 else d.toString() + "" // (int) d * 10 / 10 drops the decimal
                    ).toString() + "" + c[iteration])
        else
            coolFormat(d, iteration + 1))
    }

    fun renderEnergyBar(graphics: GuiGraphicsExtractor, font: Font, x: Int, y: Int, power: Int, maxPower: Int) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, powerBg, x, y, 74, 13)
        val uWidth = if (maxPower == 0) 0 else ((power.toFloat() / maxPower).coerceIn(0f, 1f) * 72).roundToInt()
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, powerBar, 72, 11, 0, 0, x + 1, y + 1, uWidth, 11)

        val text = if (power == -1) Component.translatable("item.tempad.creative_chronometer.infinite") else {
            if (Minecraft.getInstance().hasShiftDown()) {
                Component.literal(NumberFormat.getInstance().format(power) + "C")
            } else {
                Component.literal(coolFormat(power.toDouble(), 0) + "/" + coolFormat(maxPower.toDouble(), 0))
            }
        }
        val xOffset = (74 - font.width(text)) / 2

        graphics.scissor(x + 1, y + 1, uWidth, 11) {
            graphics.text(font, text, x + xOffset, y + 3, 0xFF000000.toInt(), false)
        }

        graphics.scissor(x + 1 + uWidth, y + 1, 72 - uWidth, 11) {
            graphics.text(font, text, x + xOffset, y + 3, Tempad.HIGHLIGHTED_ORANGE.value ?: 0, false)
        }
    }
}