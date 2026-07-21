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
import earth.terrarium.olympus.client.utils.ListenableState
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.client.TempadUI
import earth.terrarium.tempad.client.TempadUI.colored
import earth.terrarium.tempad.client.screen.tempad.TeleportScreen.Companion.sectionVisibility
import earth.terrarium.tempad.client.screen.tempad.TeleportScreen.Companion.sortMode
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.common.apps.CostAndLocation
import earth.terrarium.tempad.common.network.c2s.SyncPortalSettingsPacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.portalOffset
import earth.terrarium.tempad.common.registries.selectedPos
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.tempadId
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory
import java.util.*

class PortalSetupScreen(menu: ModMenus.PortalSetupMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.PortalSetupMenu>(null, menu, inv, title) {

    val xOffset = MutableState.of(menu.ctx.resource.portalOffset.leftRight)
    val yOffset = MutableState.of(menu.ctx.resource.portalOffset.upDown)
    val zOffset = MutableState.of(menu.ctx.resource.portalOffset.forwardBack)
    val angle = MutableState.of(menu.ctx.resource.portalOffset.angle)
    val isUpright = MutableState.of(menu.ctx.resource.portalOffset.isUpright)
    private var selected: Pair<Identifier, UUID>? = menu.ctx.resource.selectedPos?.let { it.provider to it.id }

    val search = ListenableState.of("").apply {
        registerListener {
            updateLocationPanel()
        }
    }

    private lateinit var locationList: LayoutWidget<ClearableGridLayout>

    private val locations: Map<Identifier, MutableMap<UUID, CostAndLocation>> =
        menu.appContent.locations.mapValues { (_, value) ->
            value.toMutableMap()
        }

    override fun init() {
        super.init()

        val offsetOptions = Layouts.column().withGap(2).withPosition(localLeft + 4, localTop + 20)
        offsetOptions.withChildren(
            Widgets.text(Component.translatable("app.tempad.portal_setup.offset"))
                .withColor(Tempad.ORANGE)
                .withLeftAlignment()
                .withShadow()
                .withSize(78, 11),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.translatable("app.tempad.portal_setup.left_right"))
                    .withColor(Tempad.ORANGE)
                    .withShadow()
                    .withLeftAlignment()
                    .withSize(40, 15)
                    .withTooltip(Component.translatable("app.tempad.portal_setup.left_right.desc")),
                TempadUI.floatInput(xOffset) {
                    it.withSize(36, 15)
                },
            ),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.translatable("app.tempad.portal_setup.up_down"))
                    .withColor(Tempad.ORANGE)
                    .withShadow()
                    .withLeftAlignment()
                    .withSize(40, 15)
                    .withTooltip(Component.translatable("app.tempad.portal_setup.up_down.desc")),
                TempadUI.floatInput(yOffset) {
                    it.withSize(36, 15)
                },
            ),
            Layouts.row().withGap(2).withChildren(
                Widgets
                    .text(Component.translatable("app.tempad.portal_setup.front_back"))
                    .withColor(Tempad.ORANGE)
                    .withShadow()
                    .withLeftAlignment()
                    .withSize(40, 15)
                    .withTooltip(Component.translatable("app.tempad.portal_setup.front_back.desc")),
                TempadUI.floatInput(zOffset) {
                    it.withSize(36, 15)
                },
            ),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.translatable("app.tempad.portal_setup.angle"))
                    .withColor(Tempad.ORANGE)
                    .withShadow()
                    .withLeftAlignment()
                    .withSize(40, 15),
                TempadUI.intInput(angle) {
                    it.withSize(36, 15)
                },
            ),
            Layouts.row().withGap(2).withChildren(
                Widgets.text(Component.translatable("app.tempad.portal_setup.upright"))
                    .withColor(Tempad.ORANGE)
                    .withShadow()
                    .withLeftAlignment()
                    .withSize(56, 12),
                TempadUI.toggle(isUpright).withSize(20, 12),
            )
        )

        offsetOptions.build(::addRenderableWidget)

        addRenderableWidget(Widgets.textInput(search) {
            it.withTexture(TempadUI.element)
            it.withTextColor(Tempad.ORANGE)
            it.withPlaceholder("Search...")
            it.withPlaceholderColor(Tempad.DARK_ORANGE)
            it.withPosition(localLeft + 84, localTop + 18)
            it.withSize(109, 16)
        })

        locationList = addRenderableWidget(LayoutWidget(ClearableGridLayout())).apply {
            withPosition(localLeft + 84, localTop + 36)
            withSize(109, 77)
            withContentFillWidth()
            withScrollableY(TriState.UNDEFINED)
            withScrollbarYRenderer(TempadUI.scrollbarYRenderer)
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
                display.location.name.string.contains(search.get(), ignoreCase = true)
            }.sortedBy { it.third.location.name.string.lowercase(Locale.ROOT) }

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

                        WidgetRenderers.text<Button>(display.location.name)
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

    override fun onClose() {
        super.onClose()
        SyncPortalSettingsPacket(
            xOffset.get(),
            yOffset.get(),
            zOffset.get(),
            angle.get(),
            isUpright.get(),
            selected?.first,
            selected?.second,
            menu.ctxHolder
        ).sendToServer()
    }
}