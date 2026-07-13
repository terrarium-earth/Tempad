package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.components.string.TextWidget
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.layouts.LinearViewLayout
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.tempad.client.screen.tempad.NewLocationScreen
import earth.terrarium.tempad.client.state.MutableState
import earth.terrarium.tempad.common.network.c2s.UpdateAnchorPacket
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.tempadId
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component

open class TimedoorMarkerScreen(val pos: BlockPos, name: String, color: Color, access: Boolean, locked: Boolean): Screen(
    Component.translatable("screen.tempad.marker")) {
    companion object {
        internal val publicAccess = Component.translatable("screen.tempad.marker.public_access")
    }

    val name: MutableState<String> = MutableState.of(name)
    val color: MutableState<Color> = MutableState.of(color)
    val booleanAccess: MutableState<Boolean> = MutableState.of(access)
    val locked: MutableState<Boolean> = MutableState.of(locked)

    var bgHeight = 0
    var bgWidth = 150

    fun TextWidget.configure(): TextWidget = this.withColor(MinecraftColors.GRAY).withShadow()

    override fun init() {
        super.init()

        val fields = Layouts.column().withGap(4)

        fields.withChild(FrameLayout(bgWidth - 10, 10).apply {
            addChild(Widgets.text(title).withColor(MinecraftColors.WHITE).withShadow()) {
                it.align(0f, 0f)
            }

            addChild(Layouts.row().withGap(2)
                .withChild(
                    Widgets.button {
                        it.withSize(7)
                        it.withTexture(null)
                        it.withRenderer(locked.withRenderer {
                            WidgetRenderers.withColors(
                                WidgetRenderers.icon<Button>("icons/mini/${if (it) "lock" else "unlock"}".tempadId).withShadow(),
                                MinecraftColors.DARK_GRAY,
                                MinecraftColors.WHITE,
                                MinecraftColors.GOLD
                            ).withCentered(7, 7)
                        })
                        it.withCallback {
                            locked.set(!locked.value)
                        }
                    }
                )
                .withChild(
                    Widgets.button {
                        it.withSize(7)
                        it.withTexture(null)
                        it.withRenderer(
                            WidgetRenderers.withColors(
                                WidgetRenderers.icon<Button>("icons/mini/x".tempadId).withShadow(),
                                MinecraftColors.DARK_GRAY,
                                MinecraftColors.WHITE,
                                MinecraftColors.RED
                            ).withCentered(7, 7)
                        )
                        it.withCallback {
                            onClose()
                        }
                    }
                )
            ) {
                it.align(1f, 0f)
            }
        })

        fields.withChild(SpacerElement(0, 0))

        fields.withChild(Widgets.text(NewLocationScreen.Companion.NAME_FIELD).configure())
        fields.withChild(Widgets.textInput(name) {
            it.withSize(100, 20)
        })

        fields.withChild(Widgets.text(NewLocationScreen.Companion.COLOR_FIELD).configure())
        fields.withChild(Layouts.row().apply {
            withChild(Widgets.colorPicker(color, false, {
                it.withSize(20, 20)
            }, {}))

            withChild(Widgets.colorInput(color) {
                it.withSize(80, 20)
            })
        })

        setupAccessField(fields)

        fields.build(this::addRenderableWidget)
        bgHeight = fields.height + 12
        fields.setPosition((width - bgWidth) / 2 + 5, (height - bgHeight) / 2 + 5)
    }

    open fun setupAccessField(fields: LinearViewLayout) {
        fields.withChild(LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL).apply {
            addChild(Widgets.text(publicAccess).configure()) {
                it.alignVerticallyMiddle()
                it.paddingRight(4)
            }
            addChild(Widgets.toggle(booleanAccess) {
                it.withSize(22, 12)
            }) {
                it.alignVerticallyMiddle()
            }
        })
    }

    override fun extractBackground(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float,
    ) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, UIConstants.MODAL, (width - bgWidth) / 2, (height - bgHeight) / 2, bgWidth, bgHeight)
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, UIConstants.MODAL_HEADER, (width - bgWidth) / 2 + 1, (height - bgHeight) / 2 + 1, bgWidth - 2, 15)
    }

    override fun onClose() {
        super.onClose()
        sync()
    }

    open fun sync() {
        UpdateAnchorPacket(pos, color.value, name.value, booleanAccess.get(), locked.get()).sendToServer()
    }

    override fun isPauseScreen(): Boolean = false
}