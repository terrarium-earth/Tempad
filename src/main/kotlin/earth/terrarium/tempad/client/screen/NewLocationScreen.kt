package earth.terrarium.tempad.client.screen

import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.color.ConstantColors
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.client.widgets.buttons.ColorChoice
import earth.terrarium.tempad.client.widgets.InformationPanel
import earth.terrarium.tempad.client.widgets.MapWidget
import earth.terrarium.tempad.common.network.c2s.CreateLocationPacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.btnSprites
import earth.terrarium.tempad.common.utils.globalPos
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.lwjgl.glfw.GLFW

class NewLocationScreen(menu: ModMenus.NewLocationMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.NewLocationMenu>(SPRITE, menu, inv, title) {
    companion object {
        val SPRITE = "screen/new_location".tempadId
        val NAME_FIELD = "new_location.name".toLanguageKey("menu")
        val COLOR_FIELD = "new_location.color".toLanguageKey("menu")

        val COLORS = listOf(
            Tempad.ORANGE,
            Color.RAINBOW,
            ConstantColors.red,
            ConstantColors.yellow,
            ConstantColors.lime,
            ConstantColors.cyan,
            ConstantColors.lightblue,
            ConstantColors.indigo,
            ConstantColors.violet,
            ConstantColors.hotpink,
            ConstantColors.white,
            ConstantColors.black // TODO replace black
        )
    }

    var currentColor = Tempad.ORANGE
    lateinit var textInput: EditBox

    override fun init() {
        super.init()

        addRenderableOnly(MapWidget(localLeft + 101, localTop + 21, 92, 5))

        val mapFrameLayout = FrameLayout(localLeft + 105, localTop + 25, 86, 86).setMinDimensions(86, 86)
        val posInfoLayout = LinearLayout(0,0, LinearLayout.Orientation.VERTICAL)
        mapFrameLayout.addChild(posInfoLayout) { it.alignVerticallyBottom().alignHorizontallyRight() }

        minecraft?.player?.globalPos?.let { pos ->
            InformationPanel.getEntries(pos).forEach { component ->
                posInfoLayout.addChild(StringWidget(component, font).setColor(Tempad.ORANGE.value)) { it.alignHorizontallyRight() }
            }
            mapFrameLayout.addChild(StringWidget(Component.translatable(pos.dimension().location().toLanguageKey("dimension")), font).setColor(Tempad.ORANGE.value)) {
                it.alignVerticallyTop()
                it.alignHorizontallyLeft()
            }
        }

        mapFrameLayout.arrangeElements()
        mapFrameLayout.visitWidgets { addRenderableWidget(it) }

        addRenderableWidget(
            StringWidget(
                this.localLeft + 4,
                this.localTop + 23,
                100,
                8,
                COLOR_FIELD,
                font
            ).setColor(Tempad.ORANGE.value).alignLeft()
        )

        val colorLayout = GridLayout(localLeft + 4, localTop + 36).spacing(2)

        val rowHelper = colorLayout.createRowHelper(6)

        Color.initRainbow()

        COLORS.forEach { color ->
            rowHelper.addChild(ColorChoice(color, CommonComponents.EMPTY) { currentColor = it })
        }

        colorLayout.arrangeElements()
        colorLayout.visitWidgets { addRenderableWidget(it) }

        addRenderableWidget(
            StringWidget(
                this.localLeft + 4,
                this.localTop + 72,
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
                this.localTop + 87, 84, 8, CommonComponents.EMPTY
            )
        )

        textInput.setMaxLength(32)
        textInput.isBordered = false
        textInput.fgColor
        textInput.setTextColor(Tempad.ORANGE.value)

        addRenderableWidget(ImageButton(
            this.localLeft + 84, this.localTop + 100, 14, 14,
            "save".btnSprites()
        ) {
            CreateLocationPacket(textInput.value, currentColor).sendToServer()
            minecraft?.setScreen(null)
        }).setTooltip(Tooltip.create(CommonComponents.GUI_DONE))
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