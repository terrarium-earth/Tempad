package earth.terrarium.tempad.client.screen.guide

import com.teamresourceful.resourcefullib.common.utils.TriState
import com.teamresourceful.resourcefullibkt.common.id
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.compound.LayoutWidget
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.components.string.MultilineTextWidget
import earth.terrarium.olympus.client.components.string.TextWidget
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.ClearableGridLayout
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.TempadUI.colored
import earth.terrarium.tempad.client.screen.ExtendedWikiScreen
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.client.widgets.CraftingRecipeWidget
import earth.terrarium.tempad.client.widgets.UnclickableWidget
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.registries.ModEntities
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.utils.translatable
import earth.terrarium.tempad.data.client.ModLang
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import kotlin.math.ceil

class KnowledgeScreen() : Screen(ModItems.knowledgeProjector.descriptionId.translatable), ExtendedWikiScreen {
    companion object {
        val collapsed = mutableSetOf<Component>()

        val defaultDescription: (ClearableGridLayout) -> Unit = { layout ->
            val list = layout.rows(0, 1)
            list.addChild(UnclickableWidget().apply {
                renderer =
                    WidgetRenderers.icon<AbstractWidget>("guide/images/tva".tempadId).withColor(MinecraftColors.WHITE)
                        .withCentered(64, 32)
                height = 32
            })
            list.addChild(
                Widgets.textarea(ModLang.credits, 98).setColor(Tempad.ORANGE.value).alignCenter().textAlignCenter()
            )
            list.addChild(SpacerElement(1, 4))
            list.addChild(Widgets.textarea(ModLang.travelAdvisory, 98) {
                it.setColor(Tempad.ORANGE.value)
                it.alignCenter()
                it.textAlignCenter()
                it.scale(0.25f)
            })
        }

        fun <T> T.createChapters() where T : Screen, T : ExtendedWikiScreen =
            mutableMapOf<Component, MutableMap<Identifier, (ClearableGridLayout) -> Unit>>().apply {
                put(ModLang.iron, mutableMapOf<Identifier, (ClearableGridLayout) -> Unit>().apply {
                    put(ModItems.chrononCell.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.cellOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.cellCrafting))
                        recipe("chronon_cell".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.cellUsage))
                    }
                    put(ModItems.chrononGenerator.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.chrononGenOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.chrononGenCrafting))
                        recipe("chronon_generator".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.chrononGenUsage))
                        if (CommonConfigCache.ChrononGenerator.capacity > 0) {
                            list.addChild(paragraph(ModLang.chrononGenStorage))
                        }
                    }
                    put(ModItems.locationCard.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.cardOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.cardCrafting))
                        recipe("cheap_card".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.cardUsage))
                        list.addChild(paragraph(ModLang.cardUsage2))
                        list.addChild(paragraph(ModLang.cardUsage3))
                    }
                    put(ModItems.cardWallet.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.walletOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.walletCrafting))
                        recipe("card_wallet".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.walletUsage))
                        list.addChild(paragraph(ModLang.walletUsage2))
                        list.addChild(paragraph(ModLang.walletUsage3))
                    }
                    put(ModItems.timedoorMarker.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.markerOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.markerCrafting))
                        recipe("timedoor_marker".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.markerUsage))
                        list.addChild(paragraph(ModLang.markerUsage2))
                    }
                    put(ModItems.timedoorProjector.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.projectorOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.projectorCrafting))
                        recipe("timedoor_projector".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.projectorUsage))
                        list.addChild(paragraph(ModLang.projectorUsage2))
                        list.addChild(title(ModEntities.timedoor.description))
                        list.addChild(paragraph(ModLang.projectorTimedoors))
                        list.addChild(paragraph(ModLang.projectorTimedoors2))
                    }
                    put(ModItems.locationBroadcaster.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.locationBroadcastersOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.locationBroadcastersCrafting))
                        recipe("location_broadcaster".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.locationBroadcastersUsage))
                        list.addChild(paragraph(ModLang.locationBroadcastersUsage2))
                    }
                })
                put(ModLang.steel, mutableMapOf<Identifier, (ClearableGridLayout) -> Unit>().apply {
                    put(ModItems.timeSteel.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.timeSteelOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.timeSteelCrafting))
                        (recipe("time_steel_shaped".tempadId)
                            ?: recipe("time_steel_shapeless".tempadId))?.let(list::addChild)
                        list.addChild(paragraph(ModLang.timeSteelCrafting2))
                    }
                    put(ModItems.chrononBattery.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.chrononBatteryOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.chrononBatteryCrafting))
                        recipe("chronon_battery".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.chrononBatteryUsage))
                    }
                    put(ModItems.chronometer.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.chronometerOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.chronometerCrafting))
                        recipe("chronometer".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.chronometerUsage))
                        if (CommonConfigCache.Chronometer.capacity > 0) {
                            list.addChild(paragraph(ModLang.chronometerInternalStorage))
                        }
                    }
                    put(ModItems.chronomark.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.chronomarkOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.chronomarkCrafting))
                        recipe("chronomark".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.chronomarkUsage))
                        list.addChild(paragraph(ModLang.chronomarkUsage2))
                    }
                    put(ModItems.tempad.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.tempadOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.tempadCrafting))
                        recipe("tempad".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.tempadUsage))
                        list.addChild(paragraph(ModLang.tempadUsage2))
                        list.addChild(title(ModEntities.timedoor.description))
                        list.addChild(paragraph(ModLang.tempadTimedoors))
                        list.addChild(paragraph(ModLang.tempadTimedoors2))
                        list.addChild(title(ModLang.tempadApps))
                        list.addChild(subtitle("app.tempad.teleport".translatable))
                        list.addChild(paragraph(ModLang.tempadAppTeleport))
                        list.addChild(paragraph(ModLang.tempadAppTeleport2))
                        list.addChild(paragraph(ModLang.tempadAppTeleport3))
                        list.addChild(subtitle("app.tempad.new_location".translatable))
                        list.addChild(paragraph(ModLang.tempadAppNewLocation))
                        list.addChild(subtitle("app.tempad.travel_timeline".translatable))
                        list.addChild(paragraph(ModLang.tempadAppTravelTimeline))
                        list.addChild(subtitle("app.tempad.settings".translatable))
                        list.addChild(paragraph(ModLang.tempadAppSettings))
                        list.addChild(subtitle("app.tempad.portal_setup".translatable))
                        list.addChild(paragraph(ModLang.tempadAppPortalSetup))

                    }
                    put(ModItems.workstation.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.workstationOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.workstationCrafting))
                        recipe("workstation".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.workstationUsage))
                        list.addChild(paragraph(ModLang.workstationUsage2))
                        list.addChild(paragraph(ModLang.workstationUsage3))
                    }
                    put(ModItems.timeTwister.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.timeTwisterOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.timeTwisterCrafting))
                        recipe("time_twister".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.timeTwisterUsage))
                        list.addChild(paragraph(ModLang.timeTwisterUsage2))
                        list.addChild(paragraph(ModLang.timeTwisterUsage3))
                    }
                    put(ModItems.screeningDevice.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.screeningDeviceOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.screeningDeviceCrafting))
                        recipe("screening_device".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.screeningDeviceUsage))
                    }
                    put(ModItems.metronome.id) {
                        val list = it.rowSpacing(4).rows(0, 1)
                        list.addChild(paragraph(ModLang.metronomesOverview))
                        list.addChild(title(ModLang.crafting))
                        list.addChild(paragraph(ModLang.metronomesCrafting))
                        recipe("metronome".tempadId)?.let(list::addChild)
                        list.addChild(title(ModLang.usage))
                        list.addChild(paragraph(ModLang.metronomesUsage))
                        list.addChild(paragraph(ModLang.metronomesUsage2))
                        if (CommonConfigCache.Metronome.scaleGeneration) {
                            list.addChild(paragraph(ModLang.metronomesMultiCharge))
                        } else {
                            list.addChild(paragraph(ModLang.metronomesNoMultiCharge))
                        }
                    }
                })

            }

        fun ExtendedWikiScreen.recipe(id: Identifier): CraftingRecipeWidget? {
            val recipe = CraftingRecipeWidget.create(id)
            recipe?.let { this.tickers.add(it::tick) }
            return recipe
        }

        fun closestPercent(percent: Float): Float {
            val minecraft = Minecraft.getInstance()
            val scale = minecraft.options.guiScale().get().takeIf { it > 0 } ?: minecraft.window.calculateScale(0, minecraft.isEnforceUnicode)
            return ceil(scale * percent) / scale
        }

        fun <T> T.paragraph(text: Component): MultilineTextWidget? where T : Screen, T : ExtendedWikiScreen {
            if (Minecraft.getInstance() == null) return null
            val text = Widgets.textarea(text, 98)
            text.scale(closestPercent(0.7f))
            text.alignLeft()
            text.setColor(Tempad.ORANGE.value)
            text.clickActionCallback(::handleComponentClicked)
            return text
        }

        fun title(text: Component): TextWidget? {
            if (Minecraft.getInstance() == null) return null
            return Widgets.text(MutableComponent.create(text.contents).withStyle(ChatFormatting.UNDERLINE)) {
                it.withLeftAlignment().withColor(Tempad.ORANGE)
            }
        }

        fun subtitle(text: Component): MultilineTextWidget? {
            if (Minecraft.getInstance() == null) return null
            return Widgets.textarea(text, 98) {
                it.scale(closestPercent(0.8f))
                it.alignLeft()
                it.setColor(Tempad.ORANGE.value)
            }
        }

        fun initTableOfContents(
            chapters: Map<Component, Map<Identifier, (ClearableGridLayout) -> Unit>>,
            tableOfContentsWidget: LayoutWidget<ClearableGridLayout>,
            selected: MutableState<ItemStack?>,
            titleSetter: (Component) -> Unit,
            descSetter: ((ClearableGridLayout) -> Unit) -> Unit,
        ) {
            tableOfContentsWidget.withContents {
                it.clear()
                var list = it.rows(0, 1)
                for ((category, contents) in chapters) {
                    list.addChild(Widgets.button {
                        it.withSize(72, 12)
                        it.withTexture(null)
                        it.withRenderer { graphics, ctx, partialTick ->
                            graphics.fill(ctx.x, ctx.y, ctx.x + ctx.width, ctx.y + ctx.height, 0x3aff6f00.toInt())
                            WidgetRenderers
                                .textWithChevron<Button>(category, category !in collapsed)
                                .colored()
                                .withPadding(2)
                                .render(graphics, ctx, partialTick)
                        }
                        it.withCallback {
                            if (category in collapsed) {
                                collapsed -= category
                            } else {
                                collapsed += category
                            }
                            tableOfContentsWidget.withContents {
                                it.clear()
                                initTableOfContents(chapters, tableOfContentsWidget, selected, titleSetter, descSetter)
                            }
                        }
                    })
                    if (category in collapsed) {
                        list.addChild(SpacerElement(72, 2))
                        continue
                    }
                    val grid = Layouts.columns(3)
                    for ((entry, text) in contents) {
                        val stack = ItemStack(BuiltInRegistries.ITEM.get(entry).get())
                        grid.withChild(Widgets.button {
                            it.withSize(18, 18)
                            it.withTexture(null)
                            it.withRenderer(WidgetRenderer<Button> { graphics, ctx, _ ->
                                val isSelected = selected.value?.id == entry
                                val isHovered = ctx.widget.isHoveredOrFocused

                                if (isSelected) {
                                    graphics.fill(
                                        ctx.x,
                                        ctx.y,
                                        ctx.x + ctx.width,
                                        ctx.y + ctx.height,
                                        Tempad.ORANGE.value
                                    )
                                } else if (isHovered) {
                                    graphics.fill(
                                        ctx.x,
                                        ctx.y,
                                        ctx.x + ctx.width,
                                        ctx.y + ctx.height,
                                        0x3aff6f00.toInt()
                                    )
                                }
                                graphics.item(stack, ctx.x + 1, ctx.y + 1)
                            })
                            it.withCallback {
                                selected.value = stack
                                titleSetter.invoke(stack.hoverName)
                                descSetter.invoke(text)
                            }
                        })
                    }
                    grid.arrangeElements()
                    list.addChild(LayoutWidget(grid).apply {
                        withTexture(TempadUI.element.get(true, false))
                        withContentMargin(1)
                        withScrollableY(TriState.FALSE)
                        withSize(grid.width + 2, grid.height + 2)
                    })
                    list.addChild(SpacerElement(72, 2))
                }
            }
        }
    }

    var leftPos = 0
    var topPos = 0
    var imageWidth = 198
    var imageHeight = 118
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
        leftPos = (width - imageWidth) / 2
        topPos = (height - imageHeight) / 2
        addRenderableWidget(Widgets.text(ModItems.knowledgeProjector.descriptionId.translatable) {
            it.withColor(Tempad.ORANGE)
            it.withPosition(leftPos + 4, topPos + 6)
            it.withShadow()
        })

        val tableOfContents = addRenderableWidget(LayoutWidget(ClearableGridLayout())).apply {
            withPosition(leftPos + 4, topPos + 20)
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
        titleWidget = addRenderableWidget(Widgets.text(currentTitle) {
            it.withPosition(leftPos + 78, topPos + 22)
            it.withSize(116, 10)
            it.withColor(Tempad.ORANGE)
            it.withLeftAlignment()
        })

        descriptionWidget = addRenderableWidget(LayoutWidget(ClearableGridLayout())).apply {
            withPosition(leftPos + 78, topPos + 32)
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

        initTableOfContents(chapters, tableOfContents, selected, {title -> currentTitle = title}, {text -> description = text})

        descriptionWidget?.withContents(description)
    }

    override fun extractBackground(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float,
    ) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TempadUI.element.get(true, false), leftPos, topPos, imageWidth, imageHeight)
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



    override fun tick() {
        for (function in tickers) {
            function()
        }
    }

    override fun isPauseScreen(): Boolean = false
}