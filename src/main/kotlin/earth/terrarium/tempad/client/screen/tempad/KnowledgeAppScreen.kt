package earth.terrarium.tempad.client.screen.tempad

import com.teamresourceful.resourcefullib.common.utils.TriState
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.compound.LayoutWidget
import earth.terrarium.olympus.client.components.string.TextWidget
import earth.terrarium.olympus.client.ui.ClearableGridLayout
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.screen.ExtendedWikiScreen
import earth.terrarium.tempad.client.screen.guide.KnowledgeScreen.Companion.createChapters
import earth.terrarium.tempad.client.screen.guide.KnowledgeScreen.Companion.defaultDescription
import earth.terrarium.tempad.client.screen.guide.KnowledgeScreen.Companion.initTableOfContents
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.data.client.ModLang
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

class KnowledgeAppScreen(menu: ModMenus.KnowledgeMenu, inv: Inventory, title: Component) : AbstractTempadScreen<ModMenus.KnowledgeMenu>(null, menu, inv, title), ExtendedWikiScreen {
    var titleWidget: TextWidget? = null
    var currentTitle: Component = ModLang.overview
        set(value) {
            field = value
            titleWidget?.message = value
        }

    var descriptionWidget: LayoutWidget<ClearableGridLayout>? = null
    var description: (ClearableGridLayout) -> Unit = defaultDescription
        set(value) {
            field = value
            descriptionWidget?.withContents {
                it.clear()
                value.invoke(it)
            }?.withScrollY(-3)
        }

    val selected = MutableState.of<ItemStack?>(null)
    val chapters = createChapters()
    override val tickers: MutableList<() -> Unit> = mutableListOf()

    override fun init() {
        super.init()

        val tableOfContents = addRenderableWidget(LayoutWidget(ClearableGridLayout())).apply {
            withPosition(localLeft + 4, localTop + 20)
            withSize(72, 94)
            withScrollableY(TriState.TRUE)
            withScrollbarYRenderer(TempadUI.scrollbarYRenderer)
            withTexture(TempadUI.element.get(true, false))
            withOverscrollY(2)
            withOverscrollX(2)
            withContentMargin(1)
            withWidthCallback { widget, layout ->
                layout.visitWidgets { child: AbstractWidget ->
                    child.takeIf { it.width > 18 && it is Button }?.setWidth(widget.viewWidth - 4)
                }
            }
        }

        initTableOfContents(chapters, tableOfContents, selected, {title -> currentTitle = title}, {text -> description = text})

        titleWidget = addRenderableWidget(Widgets.text(currentTitle, { it ->
            it.withPosition(localLeft + 78, localTop + 22)
            it.withSize(116, 10)
            it.withColor(Tempad.ORANGE)
            it.withLeftAlignment()
        }))

        descriptionWidget = addRenderableWidget(LayoutWidget(ClearableGridLayout())).apply {
            withPosition(localLeft + 78, localTop + 32)
            withSize(116, 82)
            withScrollableY(TriState.UNDEFINED)
            withScrollbarYRenderer(TempadUI.scrollbarYRenderer)
            withTexture(TempadUI.element.get(true, false))
            withOverscroll(3, 3)
            withContentMargin(1)
            withWidthCallback { widget, layout ->
                layout.visitWidgets { child: AbstractWidget ->
                    child.setWidth(widget.viewWidth - 6)
                }
            }
        }

        descriptionWidget?.withContents(description)
    }

    override fun handleComponentClicked(style: Style?): Boolean {
        if (style == null) return false
        if (style.clickEvent?.action() == ClickEvent.Action.CUSTOM) {
            val item = (style.clickEvent as? ClickEvent.Custom)?.id() ?: return false
            val stack = ItemStack(BuiltInRegistries.ITEM.get(item).get())
            currentTitle = stack.hoverName
            selected.value = stack
            for ((_, entries) in chapters) {
                val newDesc = entries[item] ?: continue
                description = newDesc
            }
            return true
        }
        return false
    }

    override fun containerTick() {
        for (function in tickers) {
            function()
        }
    }
}