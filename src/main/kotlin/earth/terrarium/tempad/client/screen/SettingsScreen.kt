package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.client.widgets.LabeledWidget
import earth.terrarium.tempad.client.widgets.colored.ColoredButton
import earth.terrarium.tempad.client.widgets.colored.ColoredDropdown
import earth.terrarium.tempad.client.widgets.colored.ColoredList
import earth.terrarium.tempad.common.data.*
import earth.terrarium.tempad.common.network.c2s.SaveSettingsPacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.defaultMacro
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import java.util.*

class SettingsScreen(menu: ModMenus.SettingsMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.SettingsMenu>(null, menu, inv, title) {

    var defaultMacro: ResourceLocation = menu.ctx.stack.defaultMacro
    var defaultApp: ResourceLocation = menu.ctx.stack.defaultApp

    var settings: ColoredList? = null

    override fun init() {
        super.init()

        settings = addRenderableWidget(ColoredList(190, 94, 2))

        settings?.setPosition(localLeft + 4, localTop + 20)

        settings?.add(LabeledWidget(
            "settings.default_macro".toLanguageKey("app"),
            "settings.default_macro.subtitle".toLanguageKey("app"),
            6,
            ColoredDropdown(
                94,
                MacroRegistry.getIds().toList(),
                { Component.translatable(it.toLanguageKey("macro")) },
                defaultMacro,
            ) { defaultMacro = it }
        ))

        settings?.add(LabeledWidget(
            "settings.default_app".toLanguageKey("app"),
            "settings.default_app.subtitle".toLanguageKey("app"),
            6,
            ColoredDropdown(
                94,
                AppRegistry.getIds().toList(),
                { Component.translatable(it.toLanguageKey("app")) },
                defaultApp,
            ) { defaultApp = it }
        ))

        val layout = FrameLayout(localLeft, localTop, 198, 118).setMinDimensions(198, 118)

        layout.addChild(ColoredButton(CommonComponents.GUI_DONE) {
            SaveSettingsPacket(menu.ctxHolder, defaultApp, defaultMacro).sendToServer()
        }) { it.alignHorizontallyRight().alignVerticallyBottom().padding(4) }

        layout.arrangeElements()
        layout.visitWidgets { addRenderableWidget(it) }
    }

    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
        super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        if (settings?.isDragging == true) {
            settings?.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        }
        return true
    }
}