package earth.terrarium.tempad.client.screen

import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.client.screen.TeleportScreen.Companion.imgBtn
import earth.terrarium.tempad.client.widgets.LabeledWidget
import earth.terrarium.tempad.client.widgets.colored.ColoredDropdown
import earth.terrarium.tempad.client.widgets.colored.ColoredList
import earth.terrarium.tempad.common.data.*
import earth.terrarium.tempad.common.network.c2s.SaveSettingsPacket
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.defaultMacro
import earth.terrarium.tempad.common.registries.organizationMethod
import earth.terrarium.tempad.common.utils.sendToServer
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import java.util.*

class SettingsScreen(menu: ModMenus.SettingsMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.SettingsMenu>(null, menu, inv, title) {

    var defaultMacro: ResourceLocation = menu.stack.defaultMacro
    var defaultApp: ResourceLocation = menu.stack.defaultApp
    var organizationMethod: OrganizationMethod = menu.stack.organizationMethod

    var settings: ColoredList? = null

    override fun init() {
        super.init()

        settings = addRenderableWidget(ColoredList(190, 94, 2))

        settings?.setPosition(localLeft + 4, localTop + 20)

        settings?.add(LabeledWidget(
            "settings.default_macro".toLanguageKey("app"),
            "settings.default_macro.subtitle".toLanguageKey("app"),
            8,
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
            8,
            ColoredDropdown(
                94,
                AppRegistry.getIds().toList(),
                { Component.translatable(it.toLanguageKey("app")) },
                defaultApp,
            ) { defaultApp = it }
        ))

        settings?.add(LabeledWidget(
            "settings.organization_method".toLanguageKey("app"),
            "settings.organization_method.subtitle".toLanguageKey("app"),
            8,
            ColoredDropdown(
                94,
                OrganizationMethod.entries,
                { Component.translatable("organization_method.tempad." + it.toString().lowercase(Locale.ROOT)) },
                organizationMethod,
            ) { organizationMethod = it }
        ))

        val layout = FrameLayout(localLeft + 4, localTop + 100, 190, 14).setMinDimensions(190, 14)

        layout.addChild(imgBtn("save") {
            SaveSettingsPacket(menu.ctx, defaultApp, defaultMacro, organizationMethod).sendToServer()
        }) { it.alignHorizontallyRight().alignVerticallyBottom() }

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