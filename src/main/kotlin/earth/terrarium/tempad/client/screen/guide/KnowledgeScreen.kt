package earth.terrarium.tempad.client.screen.guide

import com.mojang.blaze3d.systems.RenderSystem
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
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
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.client.widgets.UnclickableWidget
import earth.terrarium.tempad.client.widgets.recipe
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.utils.translatable
import earth.terrarium.tempad.data.client.ModLang
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.text.NumberFormat

class KnowledgeScreen() : BaseCursorScreen(ModLang.title.translatable) {
    companion object {
        val collapsed = mutableSetOf<Component>()
    }

    fun paragraph(key: String, vararg item: Any): MultilineTextWidget {
        var args = item.map {
            return@map if (it is Item) {
                Component.translatable(it.descriptionId)
                    .withStyle(Style.EMPTY
                        .withColor(Tempad.HIGHLIGHTED_ORANGE.value)
                        .withUnderlined(true)
                        .withClickEvent(ClickEvent(ClickEvent.Action.CHANGE_PAGE, it.id.toString()))
                    )
            } else if(it is String) {
                Component.literal(it).withColor(Tempad.HIGHLIGHTED_ORANGE.value)
            } else {
                it as? Component
            }
        }.toTypedArray()
        return paragraph(Component.translatable(key, *args))
    }

    fun paragraph(text: Component): MultilineTextWidget {
        val text = Widgets.textarea(text, 98)
        text.scale(0.7f)
        text.alignLeft()
        text.setColor(Tempad.ORANGE.value)
        text.clickActionCallback(::handleComponentClicked)
        return text
    }

    fun title(text: Component): TextWidget {
        return Widgets.text(MutableComponent.create(text.contents).withStyle(ChatFormatting.UNDERLINE)) {
            it.withLeftAlignment().withColor(Tempad.ORANGE)
        }
    }

    fun subTitle(text: Component): MultilineTextWidget {
        return Widgets.textarea(text, 98) {
            it.scale(0.8f)
            it.alignLeft()
            it.setColor(Tempad.ORANGE.value)
        }
    }

    var leftPos = 0
    var topPos = 0
    var manualLeftPos: Int? = null
    var manualTopPos: Int? = null
    var imageWidth = 198
    var imageHeight = 118
    var age = 0

    val selected = MutableState.of<ItemStack?>(null)
    val currentTitle = MutableState.of<Component>(ModLang.overview.translatable)
    val description = MutableState.of<(ClearableGridLayout) -> Unit>({ layout: ClearableGridLayout ->
        val list = layout.rows(0, 1)
        list.addChild(UnclickableWidget().apply {
            renderer = WidgetRenderers.icon<AbstractWidget>("guide/images/tva".tempadId).withColor(MinecraftColors.WHITE).withCentered(64, 32)
            height = 32
        })
        list.addChild(Widgets.textarea(ModLang.credits.translatable, 98).setColor(Tempad.ORANGE.value).alignCenter().textAlignCenter())
        list.addChild(SpacerElement(1, 4))
        list.addChild(Widgets.textarea(ModLang.travelAdvisory.translatable, 98) {
            it.setColor(Tempad.ORANGE.value)
            it.alignCenter()
            it.textAlignCenter()
            it.scale(0.25f)
        })
    })

    val chapters = mutableMapOf<Component, MutableMap<ResourceLocation, (ClearableGridLayout) -> Unit>>().apply {
        put(ModLang.iron.translatable, mutableMapOf<ResourceLocation, (ClearableGridLayout) -> Unit>().apply {
            put(ModItems.chrononCell.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.chrononHeader.translatable))
                list.addChild(paragraph(ModLang.chronons))
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.cellCrafting))
                this@KnowledgeScreen.recipe("chronon_cell".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                val cap = Component.literal(NumberFormat.getInstance().format(CommonConfigCache.Capacitor.capacity)).withColor(Tempad.HIGHLIGHTED_ORANGE.value)
                list.addChild(paragraph(Component.translatable(ModLang.cellUsage, cap)))
            }
            put(ModItems.chrononGenerator.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.genCrafting, ModItems.chronometer, ModItems.metronome))
                this@KnowledgeScreen.recipe("chronometer".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.genUsage, ModItems.metronome))
            }
            put(ModItems.locationCard.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.cardCrafting, ModItems.timeSteel))
                this@KnowledgeScreen.recipe("location_card".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.cardUsage, ModItems.timedoorMarker, ModItems.chronomark, ModItems.tempad,
                    ModItems.timedoorMarker, ModItems.chronomark, ModItems.tempad, ModItems.cardWallet, ModItems.timedoorProjector))
            }
            put(ModItems.cardWallet.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.walletCrafting, ModItems.locationCard, ModItems.timedoorProjector, ModItems.locationCard))
                this@KnowledgeScreen.recipe("card_wallet".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.walletUsage, ModItems.tempad))
            }
            put(ModItems.timedoorMarker.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.markerCrafting))
                this@KnowledgeScreen.recipe("timedoor_marker".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.markerUsage, ModItems.locationCard, ModItems.locationCard,
                    ModItems.timedoorProjector, ModItems.tempad, ModItems.chronomark, ModItems.locationCard))
            }
            put(ModItems.timedoorProjector.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.projectorCrafting, ModItems.tempad, ModItems.chrononCell))
                this@KnowledgeScreen.recipe("timedoor_projector".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.projectorUsage, ModItems.locationCard))
                list.addChild(paragraph(ModLang.projectorUsage2))
            }
            put(ModItems.locationBroadcaster.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.broadcasterCrafting, ModItems.tempad))
                this@KnowledgeScreen.recipe("location_broadcaster".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.broadcasterUsage, ModItems.tempad))
                list.addChild(paragraph(ModLang.broadcasterUsage2))
            }
        })
        put(ModLang.steel.translatable, mutableMapOf<ResourceLocation, (ClearableGridLayout) -> Unit>().apply {
            put(ModItems.timeSteel.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.timeSteelUsage))
                list.addChild(title(ModLang.crafting.translatable))
                (this@KnowledgeScreen.recipe("time_steel_shaped".tempadId) ?: this@KnowledgeScreen.recipe("time_steel_shapeless".tempadId))?.let(list::addChild)
                list.addChild(paragraph(ModLang.timeSteelCrafting))
                list.addChild(paragraph(ModLang.timeSteelCrafting2))
            }
            put(ModItems.chrononBattery.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.batteryCrafting, ModItems.timeSteel, ModItems.chrononCell))
                this@KnowledgeScreen.recipe("chronon_battery".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.batteryUsage, ModItems.chronometer, ModItems.chrononGenerator, ModItems.metronome))
            }
            put(ModItems.chronometer.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.chronometerCrafting, ModItems.timeSteel, ModItems.chrononBattery))
                this@KnowledgeScreen.recipe("chronometer".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.chronometerUsage, ModItems.chrononGenerator, ModItems.chrononGenerator))
            }
            put(ModItems.chronomark.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.chronomarkCrafting, ModItems.timeSteel))
                this@KnowledgeScreen.recipe("chronomark".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.chronomarkUsage, ModItems.locationCard, ModItems.timedoorProjector, ModItems.locationCard))

            }
            put(ModItems.tempad.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.tempadUsage, ModItems.timeTwister))
                list.addChild(title(ModLang.apps.translatable))
                list.addChild(paragraph(ModLang.tempadAppUsage))
                list.addChild(title(ModLang.newLocationAppTitle.translatable))
                list.addChild(paragraph(ModLang.newLocationApp))
                list.addChild(title(ModLang.teleportTitle.translatable))
                list.addChild(paragraph(ModLang.teleportApp, ModItems.timedoorMarker, ModItems.chronomark))
                list.addChild(subTitle(ModLang.teleportSortingTitle.translatable))
                list.addChild(paragraph(ModLang.teleportSorting))
                list.addChild(subTitle(ModLang.teleportLocationTitle.translatable))
                list.addChild(paragraph(ModLang.teleportLocationManagement))
                list.addChild(title(ModLang.portalSetupTitle.translatable))
                list.addChild(paragraph(ModLang.portalSetup, ModItems.workstation, ModItems.workstation))
                list.addChild(title(ModLang.travelTimelineTitle.translatable))
                list.addChild(paragraph(ModLang.travelTimeline, ModItems.timeTwister, ModItems.timeTwister))
                list.addChild(title(ModLang.settingsTitle.translatable))
                list.addChild(paragraph(ModLang.settings))
            }
            put(ModItems.workstation.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.workstationCrafting, ModItems.tempad, ModItems.timeSteel))
                this@KnowledgeScreen.recipe("workstation".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.workstationUsage, ModItems.tempad, ModItems.tempad))
                list.addChild(paragraph(ModLang.workstationUpgrades, ModItems.tempad, ModItems.tempad, ModItems.locationBroadcaster, ModItems.screeningDevice))
                list.addChild(paragraph(ModLang.workstationTerminal, ModItems.tempad, ModItems.timedoorProjector))
                list.addChild(paragraph(ModLang.workstationApp, ModItems.tempad))
            }
            put(ModItems.timeTwister.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.twisterCrafting, ModItems.timeSteel, ModItems.chrononBattery))
                this@KnowledgeScreen.recipe("time_twister".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.twisterUsage))
                list.addChild(paragraph(ModLang.twisterUsage2))
                list.addChild(paragraph(ModLang.twisterApp, ModItems.tempad, ModItems.tempad, ModItems.tempad))
            }
            put(ModItems.screeningDevice.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.screeningCrafting, ModItems.locationBroadcaster, ModItems.timeSteel))
                this@KnowledgeScreen.recipe("screening_device".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.screeningUsage, ModItems.tempad))
            }
            put(ModItems.metronome.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(title(ModLang.crafting.translatable))
                list.addChild(paragraph(ModLang.metronomeCrafting, ModItems.timeSteel, ModItems.chrononBattery))
                this@KnowledgeScreen.recipe("metronome".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage.translatable))
                list.addChild(paragraph(ModLang.metronomeBooting, ModItems.chrononCell, ModItems.chrononBattery))
                list.addChild(paragraph(ModLang.metronomeBattery))
                list.addChild(paragraph(ModLang.metronomeSyncing))
            }
        })
    }

    override fun init() {
        super.init()
        this.clearWidgets()
        leftPos = manualLeftPos ?: ((width - imageWidth) / 2)
        topPos = manualTopPos ?: ((height - imageHeight) / 2)
        if (manualTopPos == null) {
            addRenderableWidget(Widgets.text(ModItems.handbook.description) {
                it.withColor(Tempad.ORANGE)
                it.withPosition(leftPos + 4, topPos + 6)
            })
        }

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

        tableOfContents.withContents {
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
                        this.init()
                    }
                })
                if (category in collapsed) continue
                val grid = Layouts.columns(3)
                for ((entry, text) in contents) {
                    val stack = BuiltInRegistries.ITEM.get(entry).defaultInstance
                    grid.withChild(Widgets.button {
                        it.withSize(18, 18)
                        it.withTexture(null)
                        it.withRenderer(WidgetRenderer<Button> { graphics, ctx, _ ->
                            val isSelected = selected.value?.id == entry
                            val isHovered = ctx.widget.isHoveredOrFocused

                            if (isSelected) {
                                graphics.fill(ctx.x, ctx.y, ctx.x + ctx.width, ctx.y + ctx.height, Tempad.ORANGE.value)
                            } else if (isHovered) {
                                graphics.fill(ctx.x, ctx.y, ctx.x + ctx.width, ctx.y + ctx.height, 0x3aff6f00.toInt())
                            }

                            graphics.renderItem(stack, ctx.x + 1, ctx.y + 1)
                        })
                        it.withCallback {
                            selected.value = stack
                            description.value = text
                            currentTitle.value = stack.hoverName
                            this.init()
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

        addRenderableWidget(Widgets.text(currentTitle.value).withColor(Tempad.ORANGE).withPosition(leftPos + 78, topPos + 22))

        val information = addRenderableWidget(LayoutWidget(ClearableGridLayout())).apply {
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

        information.withContents(description.value)
    }

    override fun renderBackground(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick)
        RenderSystem.enableBlend()
        graphics.blitSprite(TempadUI.element.get(true, false), leftPos, topPos, imageWidth, imageHeight)
    }

    override fun render(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        f: Float,
    ) {
        RenderSystem.enableBlend()
        super.render(graphics, mouseX, mouseY, f)
    }

    override fun handleComponentClicked(style: Style?): Boolean {
        if (style == null) return false
        if (style.clickEvent?.action == ClickEvent.Action.CHANGE_PAGE) {
            val item = style.clickEvent?.let { ResourceLocation.tryParse(it.value) } ?: return false
            val stack = BuiltInRegistries.ITEM.get(item).defaultInstance
            currentTitle.value = stack.hoverName
            selected.value = stack
            for ((_, entries) in chapters) {
                val newDesc = entries[item] ?: continue
                description.value = newDesc
            }
            init()
            return true
        }
        return false
    }

    override fun isPauseScreen(): Boolean = false
}