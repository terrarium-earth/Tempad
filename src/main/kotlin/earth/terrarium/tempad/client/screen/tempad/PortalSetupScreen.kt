package earth.terrarium.tempad.client.screen.tempad

import com.teamresourceful.resourcefullib.common.color.ConstantColors
import com.teamresourceful.resourcefullib.common.utils.TriState
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.compound.LayoutWidget
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.ClearableGridLayout
import earth.terrarium.olympus.client.ui.UIIcons
import earth.terrarium.olympus.client.utils.ListenableState
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.TempadUI.colored
import earth.terrarium.tempad.client.screen.tempad.TeleportScreen.Companion.sectionVisibility
import earth.terrarium.tempad.client.screen.tempad.TeleportScreen.Companion.sortMode
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.common.network.c2s.SyncPortalSettingsPacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.portalOffset
import earth.terrarium.tempad.common.registries.selectedPos
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import java.util.Locale
import java.util.UUID
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set

class PortalSetupScreen(menu: ModMenus.PortalSetupMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.PortalSetupMenu>(null, menu, inv, title) {

    val xOffset = MutableState.of(menu.ctx.stack.portalOffset.x)
    val yOffset = MutableState.of(menu.ctx.stack.portalOffset.y)
    val zOffset = MutableState.of(menu.ctx.stack.portalOffset.z)
    val angle = MutableState.of(menu.ctx.stack.portalOffset.angle)
    val isVertical = MutableState.of(menu.ctx.stack.portalOffset.isVertical)
    private var selected: Pair<ResourceLocation, UUID>? = menu.ctx.stack.selectedPos?.let { it.provider to it.id }

    val search = ListenableState.of("").apply {
        registerListener {
            updateLocationPanel()
        }
    }

    private lateinit var locationList: LayoutWidget<ClearableGridLayout>

    private val locations: Map<ResourceLocation, MutableMap<UUID, NamedGlobalVec3>> =
        menu.appContent.locations.mapValues { (_, value) ->
            value.toMutableMap()
        }

    override fun init() {
        super.init()

        val offsetOptions = Layouts.column().withGap(2).withPosition(localLeft + 4, localTop + 18)

        offsetOptions.withChildren(
            Widgets.text(Component.literal("Offset")).withColor(Tempad.ORANGE).withShadow(),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.literal("X: ")).withColor(Tempad.ORANGE).withShadow().withLeftAlignment().withSize(16, 16),
                Widgets.button {
                    it.withRenderer(
                        WidgetRenderers.icon<Button>(UIIcons.CHEVRON_DOWN).withColor(MinecraftColors.BLACK)
                            .withCentered(10, 10)
                    )
                    it.withTexture(TempadUI.button)
                    it.withSize(16)
                },
                TempadUI.floatInput(xOffset) {
                    it.withSize(24, 16)
                },
                Widgets.button {
                    it.withRenderer(
                        WidgetRenderers.icon<Button>(UIIcons.CHEVRON_UP).withColor(MinecraftColors.BLACK)
                            .withCentered(10, 10)
                    )
                    it.withTexture(TempadUI.button)
                    it.withSize(16)
                }
            ),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.literal("Y: ")).withColor(Tempad.ORANGE).withShadow().withLeftAlignment().withSize(16, 16),
                Widgets.button {
                    it.withRenderer(
                        WidgetRenderers.icon<Button>(UIIcons.CHEVRON_DOWN).withColor(MinecraftColors.BLACK)
                            .withCentered(10, 10)
                    )
                    it.withTexture(TempadUI.button)
                    it.withSize(16)
                },
                TempadUI.floatInput(yOffset) {
                    it.withSize(24, 16)
                },
                Widgets.button {
                    it.withRenderer(
                        WidgetRenderers.icon<Button>(UIIcons.CHEVRON_UP).withColor(MinecraftColors.BLACK)
                            .withCentered(10, 10)
                    )
                    it.withTexture(TempadUI.button)
                    it.withSize(16)
                }
            ),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.literal("Z: ")).withColor(Tempad.ORANGE).withShadow().withLeftAlignment().withSize(16, 16),
                Widgets.button {
                    it.withRenderer(
                        WidgetRenderers.icon<Button>(UIIcons.CHEVRON_DOWN).withColor(MinecraftColors.BLACK)
                            .withCentered(10, 10)
                    )
                    it.withTexture(TempadUI.button)
                    it.withSize(16)
                },
                TempadUI.floatInput(zOffset) {
                    it.withSize(24, 16)
                },
                Widgets.button {
                    it.withRenderer(
                        WidgetRenderers.icon<Button>(UIIcons.CHEVRON_UP).withColor(MinecraftColors.BLACK)
                            .withCentered(10, 10)
                    )
                    it.withTexture(TempadUI.button)
                    it.withSize(16)
                }
            ),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.literal("Angle: ")).withColor(Tempad.ORANGE).withShadow().withLeftAlignment().withSize(34, 16),
                TempadUI.floatInput(angle) {
                    it.withSize(42, 16)
                },
            ),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.literal("Vertical: ")).withColor(Tempad.ORANGE).withShadow().withLeftAlignment().withSize(56, 12),
                TempadUI.toggle(isVertical).withSize(20, 12),
            )
        )

        offsetOptions.build(::addRenderableWidget)

        addRenderableWidget(Widgets.textInput(search) {
            it.withTexture(TempadUI.element)
            it.withTextColor(Tempad.ORANGE)
            it.withSize(109, 16)
            it.withPosition(localLeft + 84, localTop + 18)
        })

        locationList = addRenderableWidget(LayoutWidget(ClearableGridLayout())).apply {
            withPosition(localLeft + 84, localTop + 36)
            withSize(109, 77)
            withContentFillWidth()
            withScrollableY(TriState.UNDEFINED)
            withScrollbarYRenderer(TeleportScreen.Companion.scrollbarYRenderer)
            withTexture("element/normal".tempadId)
            withOverscrollY(2)
            withContentMargin(1)
        }

        updateLocationPanel()
        addRenderableWidget(locationList)
    }

    fun updateLocationPanel() {
        locationList.withContents {
            it.clear()
            it.initLocationPanel()
        }
    }

    fun ClearableGridLayout.initLocationPanel() {
        var list = this.rows(0, 1)

        for ((category, locations) in sortMode.get().reorganize(locations)) {
            val filtered = locations.filter { (_, _, display) ->
                display.name.string.contains(search.get(), ignoreCase = true)
            }.sortedBy { it.third.name.string.lowercase(Locale.ROOT) }

            if (filtered.isEmpty()) continue

            list.addChild(Widgets.button {
                it.withSize(110, 12)
                it.withTexture(null)
                it.withRenderer(
                    sectionVisibility.getOrDefault(category, MutableState.of(true)).withRenderer { enabled ->
                        WidgetRenderers
                            .textWithChevron<Button>(category, enabled)
                            .colored()
                            .withPadding(0, 2, 0, 4)
                    }
                )
                it.withCallback {
                    sectionVisibility[category] =
                        sectionVisibility.getOrDefault(category, MutableState.of(true)).apply { set(!get()) }
                    updateLocationPanel()
                }
            })

            if (!sectionVisibility.getOrDefault(category, MutableState.of(true)).get()) continue
            for ((provider, locationId, display) in filtered) {
                val entry = Widgets.button {
                    it.withSize(110, 12)
                    it.withTexture(null)
                    it.withRenderer(WidgetRenderer<Button> { graphics, ctx, _ ->
                        val isSelected = selected?.second == locationId && selected?.first == provider
                        val isHovered = ctx.widget.isHoveredOrFocused

                        if (isSelected) {
                            graphics.fill(ctx.x, ctx.y, ctx.x + ctx.width, ctx.y + ctx.height, Tempad.ORANGE.value)
                        } else {
                            graphics.fill(ctx.x, ctx.y, ctx.x + ctx.width, ctx.y + ctx.height, 0x3aff6f00.toInt())
                        }

                        WidgetRenderers.text<Button>(display.name)
                            .withColor(if (isSelected) ConstantColors.black else if (isHovered) Tempad.HIGHLIGHTED_ORANGE else Tempad.ORANGE)
                            .withLeftAlignment()
                            .withPadding(0, 0, 0, 2)
                            .render(graphics, ctx, 0f)
                    }.withPadding(0, 2))
                    it.withCallback {
                        selected = provider to locationId
                    }
                }
                list.addChild(entry)
            }
        }
    }

    override fun render(
        pGuiGraphics: GuiGraphics,
        pMouseX: Int,
        pMouseY: Int,
        pPartialTick: Float,
    ) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    override fun onClose() {
        super.onClose()

        SyncPortalSettingsPacket(xOffset.get(), yOffset.get(), zOffset.get(), angle.get(), isVertical.get(), selected?.first, selected?.second, menu.ctx.holder).sendToServer()
    }
}