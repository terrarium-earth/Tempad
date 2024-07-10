package earth.terrarium.tempad.client.widgets

import com.teamresourceful.resourcefullib.client.screens.CursorScreen
import earth.terrarium.olympus.client.components.base.BaseParentWidget
import earth.terrarium.tempad.Tempad
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.util.Mth
import kotlin.math.max


class HorizontalListWidget(width: Int, height: Int) : BaseParentWidget(width, height) {
    protected val items: MutableList<Item> = ArrayList()

    protected var scroll: Double = 0.0
    protected var lastWidth: Int = 0
    var isScrolling: Boolean = false
        protected set
    protected var gap: Int = 2

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
        updateScrollBar()
    }

    override fun children(): List<GuiEventListener?> {
        return items
    }

    public override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        graphics.enableScissor(x + 1, y, x + width, y + height)

        graphics.fill(x, y + 57, x + width, y + 58, Tempad.ORANGE.value)

        var x = this.x - scroll.toInt()
        this.lastWidth = 0

        for (item in items) {
            item.x = x
            item.y = y

            item.render(graphics, this.x + width / 2, this.y + height / 2, partialTicks)
            x += item.width + gap
            this.lastWidth += item.width + gap
        }

        graphics.disableScissor()
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (this.isScrolling) {
            val scrollBarHeight = (this.height / lastWidth.toDouble()) * this.height
            val scrollBarDragY = dragY / (this.height - scrollBarHeight)
            this.scroll = Mth.clamp(
                this.scroll + scrollBarDragY * this.lastWidth, 0.0,
                max(0.0, (this.lastWidth - this.height).toDouble())
            )
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        this.scroll = Mth.clamp(this.scroll - scrollX * 10 + scrollY * 10, -this.width / 2.0, max(0.0, (this.lastWidth - this.width * 0.5)))
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (clicked(mouseX, mouseY)) {
            val fakeClickX = x + width / 2
            val fakeClickY = y + height / 2
            for (entry in this.items) {
                if (entry.getRectangle().containsPoint(fakeClickX, fakeClickY)){
                    return entry.mouseClicked(fakeClickX.toDouble(), fakeClickY.toDouble(), button)
                }
            }
        }
        return false
    }

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean {
        if (i == 0) {
            this.isScrolling = false
        }
        return super.mouseReleased(d, e, i)
    }

    private fun updateLastWidth() {
        this.lastWidth = 0
        var x = this.x - scroll.toInt()
        for (item in items) {
            item.x = (x)
            item.y = (y)
            this.lastWidth += item.width
            x += item.width
        }
    }

    protected fun updateScrollBar() {
        updateLastWidth()
        this.scroll =
            Mth.clamp(this.scroll, - this.width / 2.0, max(0.0, (this.lastWidth - this.width * 0.5)))
    }

    fun scrollToBottom() {
        this.scroll = max(0.0, (this.lastWidth - this.width * 0.5))
    }

    override fun getCursor(): CursorScreen.Cursor {
        return CursorScreen.Cursor.POINTER
    }

    override fun setHeight(height: Int) {
        this.height = height
    }

    interface Item : GuiEventListener, Renderable, NarratableEntry, LayoutElement {
        override fun getRectangle(): ScreenRectangle = super<LayoutElement>.getRectangle()

        var parent: HorizontalListWidget?
    }
}