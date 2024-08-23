package earth.terrarium.tempad.client.widgets

import com.teamresourceful.resourcefullib.client.screens.CursorScreen
import earth.terrarium.olympus.client.components.base.BaseParentWidget
import earth.terrarium.tempad.Tempad
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.util.Mth
import java.util.function.Consumer
import kotlin.math.max


class HorizontalListWidget(width: Int, height: Int) : BaseParentWidget(width, height) {
    protected val items: MutableList<Item> = ArrayList()

    protected var scroll: Double = 0.0
    protected var lastWidth: Int = 0
    var isScrolling: Boolean = false
        protected set

    var current: Item? = null

    override fun clear() {
        super.clear()
        items.clear()
    }

    fun set(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        updateScrollBar()
    }

    fun add(item: Item) {
        items.add(item)
        item.parent = this
        item.height = height
        updateScrollBar()
    }

    override fun children(): List<GuiEventListener?> {
        return items
    }

    public override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        graphics.enableScissor(x + 1, y, x + width, y + height)

        var x = this.x + width / 2 - scroll.toInt() - 10
        this.lastWidth = 0

        for (item in items) {
            item.x = x
            item.y = y
            item.current = x > (this.x + width / 2) - item.width && x <= (this.x + width / 2)
            if (item.current) {
                current = item
            }
            item.render(graphics, this.x, this.y + height / 2, partialTicks)
            x += item.width
            this.lastWidth += item.width
        }

        graphics.disableScissor()
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        this.scroll = Mth.clamp(this.scroll - scrollX * 10 + scrollY * 10, 0.0, lastWidth.toDouble() - 20)
        return true
    }

    private fun updateLastWidth() {
        this.lastWidth = 0
        var x = this.x - scroll.toInt()
        for (item in items) {
            item.x = x
            item.y = y
            this.lastWidth += item.width
            x += item.width
        }
    }

    protected fun updateScrollBar() {
        updateLastWidth()
        this.scroll =
            Mth.clamp(this.scroll, 0.0, max(0.0, this.lastWidth.toDouble()))
    }

    fun scrollToBottom() {
        updateLastWidth()
        this.scroll = max(0.0, this.lastWidth.toDouble() - 20)
    }

    override fun getCursor(): CursorScreen.Cursor {
        return CursorScreen.Cursor.POINTER
    }

    override fun setHeight(height: Int) {
        this.height = height
    }

    abstract class Item : GuiEventListener, Renderable, NarratableEntry, LayoutElement {
        override fun getRectangle(): ScreenRectangle = super<LayoutElement>.getRectangle()

        internal var x: Int = 0
        internal var y: Int = 0

        internal var parent: HorizontalListWidget? = null
        internal var height: Int = 0
        internal var width: Int = 20
        internal var isFocused: Boolean = false

        var current = false

        override fun setFocused(pFocused: Boolean) {
            isFocused = pFocused
        }

        override fun isFocused(): Boolean = isFocused

        override fun setY(pY: Int) {
            y = pY
        }

        override fun getY(): Int = y

        override fun setX(pX: Int) {
            x = pX
        }

        override fun getX(): Int = x

        override fun getWidth(): Int = width

        override fun getHeight(): Int = height

        override fun narrationPriority(): NarratableEntry.NarrationPriority = NarratableEntry.NarrationPriority.NONE

        override fun updateNarration(pNarrationElementOutput: NarrationElementOutput) {}

        override fun visitWidgets(pConsumer: Consumer<AbstractWidget>) {}
    }
}