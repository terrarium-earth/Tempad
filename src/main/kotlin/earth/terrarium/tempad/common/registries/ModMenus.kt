package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.menu.MenuContentHelper
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.apps.*
import earth.terrarium.tempad.common.menu.AbstractTempadMenu
import earth.terrarium.tempad.common.menu.AbstractTemputerMenu
import earth.terrarium.tempad.common.menu.MetronomeMenu
import earth.terrarium.tempad.common.menu.MetronomeMenuData
import earth.terrarium.tempad.common.menu.WalletMenu
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import java.util.*

object ModMenus {
    val registry: ResourcefulRegistry<MenuType<*>> = ResourcefulRegistries.create(BuiltInRegistries.MENU, Tempad.MOD_ID)

    class TeleportMenu(id: Int, inv: Inventory, data: Optional<TeleportData>): AbstractTempadMenu<TeleportData>(id, inv, teleport, data)

    val teleport: MenuType<TeleportMenu> by registry.register("teleport") {
        MenuContentHelper.create(
            ::TeleportMenu,
            RecordCodecMenuContentSerializer(TeleportData.codec)
        )
    }

    class SettingsMenu(id: Int, inv: Inventory, data: Optional<SettingsData>): AbstractTempadMenu<SettingsData>(id, inv, settings, data)

    val settings: MenuType<SettingsMenu> by registry.register("settings") {
        MenuContentHelper.create(
            ::SettingsMenu,
            RecordCodecMenuContentSerializer(SettingsData.codec)
        )
    }

    class NewLocationMenu(id: Int, inv: Inventory, data: Optional<NewLocationAppData>): AbstractTempadMenu<NewLocationAppData>(id, inv, newLocation, data)

    val newLocation: MenuType<NewLocationMenu> by registry.register("new_location") {
        MenuContentHelper.create(
            ::NewLocationMenu,
            RecordCodecMenuContentSerializer(NewLocationAppData.codec)
        )
    }

    class TimelineMenu(id: Int, inv: Inventory, data: Optional<TimelineData>): AbstractTempadMenu<TimelineData>(id, inv, timeline, data)

    val timeline: MenuType<TimelineMenu> by registry.register("timeline") {
        MenuContentHelper.create(
            ::TimelineMenu,
            RecordCodecMenuContentSerializer(TimelineData.codec)
        )
    }

    class PortalSetupMenu(id: Int, inv: Inventory, data: Optional<PortalSetupData>): AbstractTempadMenu<PortalSetupData>(id, inv, portalSetup, data)

    val portalSetup: MenuType<PortalSetupMenu> by registry.register("portal_setup") {
        MenuContentHelper.create(
            ::PortalSetupMenu,
            RecordCodecMenuContentSerializer(PortalSetupData.codec)
        )
    }

    class KnowledgeMenu(id: Int, inv: Inventory, data: Optional<BasicAppContent>): AbstractTempadMenu<BasicAppContent>(id, inv, guide, data)

    val guide: MenuType<KnowledgeMenu> by registry.register("guide") {
        MenuContentHelper.create(
            ::KnowledgeMenu,
            RecordCodecMenuContentSerializer(BasicAppContent.codec)
        )
    }

    val metronome: MenuType<MetronomeMenu> by registry.register("metronome") {
        MenuContentHelper.create(
            ::MetronomeMenu,
            RecordCodecMenuContentSerializer(MetronomeMenuData.byteCodec)
        )
    }

    val wallet: MenuType<WalletMenu> by registry.register("wallet") {
        MenuType(
            ::WalletMenu,
            FeatureFlags.VANILLA_SET
        )
    }

    class TemputerMenu(id: Int, inv: Inventory): AbstractTemputerMenu(temputerTest, id)

    val temputerTest: MenuType<TemputerMenu> by registry.register("temputer_test") {
        MenuType(
            ::TemputerMenu,
            FeatureFlags.VANILLA_SET
        )
    }
}