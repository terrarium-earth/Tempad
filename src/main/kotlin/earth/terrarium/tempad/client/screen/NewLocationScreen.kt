package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.color.ConstantColors
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.client.widgets.buttons.ColorChoice
import earth.terrarium.tempad.client.widgets.InformationPanel
import earth.terrarium.tempad.client.widgets.MapWidget
import earth.terrarium.tempad.client.widgets.colored.ColoredButton
import earth.terrarium.tempad.common.network.c2s.CreateLocationPacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.globalPos
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.lwjgl.glfw.GLFW

class NewLocationScreen(menu: ModMenus.NewLocationMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.NewLocationMenu>(SPRITE, menu, inv, title) {
    companion object {
        val SPRITE = "screen/new_location".tempadId
        val NAME_FIELD = "new_location.name".toLanguageKey("app")
        val COLOR_FIELD = "new_location.color".toLanguageKey("app")

        val COLORS = listOf(
            Tempad.ORANGE,
            ConstantColors.yellow,
            ConstantColors.lime,
            ConstantColors.cyan,
            ConstantColors.purple,
            ConstantColors.red,
            ConstantColors.white,
            ConstantColors.black,
            Color.RAINBOW
        )

        fun getEntries(pos: Vec3i): List<Component> {
            return listOf(
                Component.literal("X: ${pos.x}"),
                Component.literal("Y: ${pos.y}"),
                Component.literal("Z: ${pos.z}"),
            )
        }
    }

    var currentColor = Tempad.ORANGE
    lateinit var textInput: EditBox
    lateinit var doneBtn: ColoredButton

    override fun init() {
        super.init()

        addRenderableOnly(MapWidget(localLeft + 101, localTop + 21, 92))

        val mapFrameLayout = FrameLayout(localLeft + 101, localTop + 21, 92, 92).setMinDimensions(92, 92)
        val posInfoLayout = LinearLayout(0,0, LinearLayout.Orientation.VERTICAL).spacing(2)
        mapFrameLayout.addChild(posInfoLayout) { it.alignVerticallyBottom().alignHorizontallyRight().padding(4) }

        minecraft?.player?.globalPos?.let { pos ->
            mapFrameLayout.addChild(StringWidget(Component.translatable(pos.dimension().location().toLanguageKey("dimension")), font).setColor(Tempad.ORANGE.value)) {
                it.alignVerticallyTop()
                it.alignHorizontallyLeft()
                it.padding(4)
            }

            getEntries(pos.pos).forEach { component ->
                posInfoLayout.addChild(StringWidget(component, font).setColor(Tempad.ORANGE.value)) { it.alignHorizontallyRight() }
            }
        }

        mapFrameLayout.arrangeElements()
        mapFrameLayout.visitWidgets { addRenderableWidget(it) }

        addRenderableWidget(
            StringWidget(
                this.localLeft + 4,
                this.localTop + 22,
                100,
                8,
                COLOR_FIELD,
                font
            ).setColor(Tempad.ORANGE.value).alignLeft()
        )

        val colorLayout = GridLayout(localLeft + 4, localTop + 34).spacing(2)

        val rowHelper = colorLayout.createRowHelper(6)

        for ((index, color) in COLORS.withIndex()) {
            rowHelper.addChild(ColorChoice(color) {
                colorLayout.visitWidgets { (it as ColorChoice).selected = false }
                currentColor = it.color
                it.selected = true
            }).selected = index == 0
        }

        colorLayout.arrangeElements()
        colorLayout.visitWidgets { addRenderableWidget(it) }

        addRenderableWidget(
            StringWidget(
                this.localLeft + 4,
                this.localTop + 70,
                100,
                8,
                NAME_FIELD,
                font
            ).setColor(Tempad.ORANGE.value).alignLeft()
        )

        textInput = addRenderableWidget(
            EditBox(
                this.font,
                this.localLeft + 8,
                this.localTop + 84, 84, 8, CommonComponents.EMPTY
            )
        ).apply {
            this.setMaxLength(32)
            this.isBordered = false
            setTextColor(Tempad.ORANGE.value)
            setResponder {
                doneBtn.active = it.isNotBlank()
            }
        }

        doneBtn = addRenderableWidget(ColoredButton(CommonComponents.GUI_DONE) {
            CreateLocationPacket(textInput.value, currentColor, menu.ctxHolder).sendToServer()
            this.onClose()
        }).apply {
            active = textInput.value.isNotBlank()
            y = localTop + 98
            x = localLeft + 97 - this.width
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (!textInput.isMouseOver(pMouseX, pMouseY)) {
            textInput.isFocused = false
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton)
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        if (textInput.isFocused) {
            if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
                textInput.isFocused = false
                return true
            }
            return textInput.keyPressed(pKeyCode, pScanCode, pModifiers)
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers)
    }
}