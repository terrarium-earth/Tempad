package earth.terrarium.tempad.client.screen

import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.client.screen.TeleportScreen.Companion.imgBtn
import earth.terrarium.tempad.client.widgets.LabeledWidget
import earth.terrarium.tempad.client.widgets.buttons.ButtonType
import earth.terrarium.tempad.client.widgets.buttons.FlatButton
import earth.terrarium.tempad.client.widgets.colored.ColoredDropdown
import earth.terrarium.tempad.client.widgets.colored.ColoredList
import earth.terrarium.tempad.common.data.*
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import java.util.*

class SettingsScreen(menu: ModMenus.SettingsMenu, inv: Inventory, title: Component) :
    AbstractTempadScreen<ModMenus.SettingsMenu>(null, menu, inv, title) {
    var tempadStack = inv[menu.appContent?.slotId!!]

    var defaultMacro: ResourceLocation = tempadStack.settings.defaultMacro
    var defaultApp: ResourceLocation = tempadStack.settings.defaultApp
    var organizationMethod: OrganizationMethod = tempadStack.settings.organizationMethod

    var settings: ColoredList? = null

    override fun init() {
        super.init()

        settings = addRenderableWidget(ColoredList(190, 94, 2))

        settings?.setPosition(localLeft + 4, localTop + 20)

        settings?.add(LabeledWidget(
            "settings.default_macro".toLanguageKey("menu"),
            "settings.default_macro.subtitle".toLanguageKey("menu"),
            8,
            ColoredDropdown(
                94,
                MacroRegistry.getIds().toList(),
                { Component.translatable(it.toLanguageKey("macro")) },
                defaultMacro,
            ) { defaultMacro = it }
        ))

        settings?.add(LabeledWidget(
            "settings.default_app".toLanguageKey("menu"),
            "settings.default_app.subtitle".toLanguageKey("menu"),
            8,
            ColoredDropdown(
                94,
                AppRegistry.getIds().toList(),
                { Component.translatable(it.toLanguageKey("app")) },
                defaultApp,
            ) { defaultApp = it }
        ))

        settings?.add(LabeledWidget(
            "settings.organization_method".toLanguageKey("menu"),
            "settings.organization_method.subtitle".toLanguageKey("menu"),
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
            onClose()
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