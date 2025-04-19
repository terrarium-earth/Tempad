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
import earth.terrarium.tempad.common.registries.ModEntities
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.utils.translatable
import earth.terrarium.tempad.data.client.ModLang
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class KnowledgeScreen() : BaseCursorScreen(ModLang.title) {
    companion object {
        val collapsed = mutableSetOf<Component>()

        val defaultDescription: (ClearableGridLayout) -> Unit = { layout ->
            val list = layout.rows(0, 1)
            list.addChild(UnclickableWidget().apply {
                renderer = WidgetRenderers.icon<AbstractWidget>("guide/images/tva".tempadId).withColor(MinecraftColors.WHITE).withCentered(64, 32)
                height = 32
            })
            list.addChild(Widgets.textarea(ModLang.credits, 98).setColor(Tempad.ORANGE.value).alignCenter().textAlignCenter())
            list.addChild(SpacerElement(1, 4))
            list.addChild(Widgets.textarea(ModLang.travelAdvisory, 98) {
                it.setColor(Tempad.ORANGE.value)
                it.alignCenter()
                it.textAlignCenter()
                it.scale(0.25f)
            })
        }
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

    fun subtitle(text: Component): MultilineTextWidget {
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

    var titleWidget: StringWidget? = null
    var currentTitle: Component = ModItems.handbook.descriptionId.translatable
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
            }
        }

    val selected = MutableState.of<ItemStack?>(null)

    val chapters = mutableMapOf<Component, MutableMap<ResourceLocation, (ClearableGridLayout) -> Unit>>().apply {
        put(ModLang.iron, mutableMapOf<ResourceLocation, (ClearableGridLayout) -> Unit>().apply {
            put(ModItems.chrononCell.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.cellOverview))
                list.addChild(title(ModLang.crafting))
                list.addChild(paragraph(ModLang.cellCrafting))
                this@KnowledgeScreen.recipe("chronon_cell".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage))
                list.addChild(paragraph(ModLang.cellUsage))
            }
            put(ModItems.chrononGenerator.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.chrononGenOverview))
                list.addChild(title(ModLang.crafting))
                list.addChild(paragraph(ModLang.chrononGenCrafting))
                this@KnowledgeScreen.recipe("chronon_generator".tempadId)?.let(list::addChild)
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
                this@KnowledgeScreen.recipe("cheap_card".tempadId)?.let(list::addChild)
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
                this@KnowledgeScreen.recipe("card_wallet".tempadId)?.let(list::addChild)
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
                this@KnowledgeScreen.recipe("timedoor_marker".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage))
                list.addChild(paragraph(ModLang.markerUsage))
                list.addChild(paragraph(ModLang.markerUsage2))
            }
            put(ModItems.timedoorProjector.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.projectorOverview))
                list.addChild(title(ModLang.crafting))
                list.addChild(paragraph(ModLang.projectorCrafting))
                this@KnowledgeScreen.recipe("timedoor_projector".tempadId)?.let(list::addChild)
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
                this@KnowledgeScreen.recipe("location_broadcaster".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage))
                list.addChild(paragraph(ModLang.locationBroadcastersUsage))
                list.addChild(paragraph(ModLang.locationBroadcastersUsage2))
            }
        })
        put(ModLang.steel, mutableMapOf<ResourceLocation, (ClearableGridLayout) -> Unit>().apply {
            put(ModItems.timeSteel.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.timeSteelOverview))
                list.addChild(title(ModLang.crafting))
                list.addChild(paragraph(ModLang.timeSteelCrafting))
                this@KnowledgeScreen.recipe("time_steel".tempadId)?.let(list::addChild)
                list.addChild(paragraph(ModLang.timeSteelCrafting2))
            }
            put(ModItems.chrononBattery.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.chrononBatteryOverview))
                list.addChild(title(ModLang.crafting))
                list.addChild(paragraph(ModLang.chrononBatteryCrafting))
                this@KnowledgeScreen.recipe("chronon_battery".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage))
                list.addChild(paragraph(ModLang.chrononBatteryUsage))
            }
            put(ModItems.chronometer.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.chronometerOverview))
                list.addChild(title(ModLang.crafting))
                list.addChild(paragraph(ModLang.chronometerCrafting))
                this@KnowledgeScreen.recipe("chronometer".tempadId)?.let(list::addChild)
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
                this@KnowledgeScreen.recipe("chronomark".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage))
                list.addChild(paragraph(ModLang.chronomarkUsage))
                list.addChild(paragraph(ModLang.chronomarkUsage2))
            }
            put(ModItems.tempad.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.tempadOverview))
                list.addChild(title(ModLang.crafting))
                list.addChild(paragraph(ModLang.tempadCrafting))
                this@KnowledgeScreen.recipe("tempad".tempadId)?.let(list::addChild)
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
                this@KnowledgeScreen.recipe("workstation".tempadId)?.let(list::addChild)
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
                this@KnowledgeScreen.recipe("time_twister".tempadId)?.let(list::addChild)
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
                this@KnowledgeScreen.recipe("screening_device".tempadId)?.let(list::addChild)
                list.addChild(title(ModLang.usage))
                list.addChild(paragraph(ModLang.screeningDeviceUsage))
            }
            put(ModItems.metronome.id) {
                val list = it.rowSpacing(4).rows(0, 1)
                list.addChild(paragraph(ModLang.metronomesOverview))
                list.addChild(title(ModLang.crafting))
                list.addChild(paragraph(ModLang.metronomesCrafting))
                this@KnowledgeScreen.recipe("metronome".tempadId)?.let(list::addChild)
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

    override fun init() {
        super.init()
        this.clearWidgets()
        leftPos = manualLeftPos ?: ((width - imageWidth) / 2)
        topPos = manualTopPos ?: ((height - imageHeight) / 2)
        if (manualTopPos == null) {
            addRenderableWidget(Widgets.text(ModItems.handbook.description) {
                it.withColor(Tempad.ORANGE)
                it.withPosition(leftPos + 4, topPos + 6)
                it.withShadow()
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
                            currentTitle = stack.hoverName
                            description = text
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

        titleWidget = addRenderableWidget(StringWidget(leftPos + 78, topPos + 22, 116, 10, currentTitle, font).apply {
            setColor(Tempad.ORANGE.value)
            alignLeft()
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

        descriptionWidget?.withContents(description)
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
        age++
    }

    override fun isPauseScreen(): Boolean = false
}