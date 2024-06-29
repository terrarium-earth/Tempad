package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.menu.MenuContentHelper
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.apps.TeleportData
import earth.terrarium.tempad.common.apps.SettingsData
import earth.terrarium.tempad.common.menu.TeleportMenu
import earth.terrarium.tempad.common.menu.TempadSettingsMenu
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.inventory.MenuType

object ModMenus {
    val REGISTRY: ResourcefulRegistry<MenuType<*>> = BuiltInRegistries.MENU.createRegistry(Tempad.MOD_ID)

    val TELEPORT_MENU by REGISTRY.register("teleport") {
        MenuContentHelper.create(
            ::TeleportMenu,
            RecordCodecMenuContentSerializer(TeleportData.CODEC)
        )
    }

    val SETTINGS_MENU by REGISTRY.register("settings") {
        MenuContentHelper.create(
            ::TempadSettingsMenu,
            RecordCodecMenuContentSerializer(SettingsData.CODEC)
        )
    }
}