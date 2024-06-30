package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.menu.MenuContentHelper
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.apps.NewLocationData
import earth.terrarium.tempad.common.apps.TeleportData
import earth.terrarium.tempad.common.apps.SettingsData
import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import java.util.Optional

object ModMenus {
    val REGISTRY: ResourcefulRegistry<MenuType<*>> = BuiltInRegistries.MENU.createRegistry(Tempad.MOD_ID)

    class TeleportMenu(id: Int, inv: Inventory, data: Optional<TeleportData>): AbstractTempadMenu<TeleportData>(id, inv, TELEPORT_MENU, data)

    val TELEPORT_MENU by REGISTRY.register("teleport") {
        MenuContentHelper.create(
            ::TeleportMenu,
            RecordCodecMenuContentSerializer(TeleportData.CODEC)
        )
    }

    class TempadSettingsMenu(id: Int, inv: Inventory, data: Optional<SettingsData>): AbstractTempadMenu<SettingsData>(id, inv, SETTINGS_MENU, data)

    val SETTINGS_MENU by REGISTRY.register("settings") {
        MenuContentHelper.create(
            ::TempadSettingsMenu,
            RecordCodecMenuContentSerializer(SettingsData.CODEC)
        )
    }

    class NewLocationMenu(id: Int, inv: Inventory, data: Optional<NewLocationData>): AbstractTempadMenu<NewLocationData>(id, inv, NEW_LOCATION_MENU, data)

    val NEW_LOCATION_MENU by REGISTRY.register("new_location") {
        MenuContentHelper.create(
            ::NewLocationMenu,
            RecordCodecMenuContentSerializer(NewLocationData.CODEC)
        )
    }
}