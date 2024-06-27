package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.menu.TempadSettingsMenu
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

object SettingsApp: TempadApp<SettingsData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return TempadSettingsMenu(pContainerId, pPlayerInventory, SettingsData(TempadLocations.getProviders()))
    }

    override fun getDisplayName(): Component = Component.translatable("menu.tempad.settings")

    override fun createContent(player: ServerPlayer): SettingsData = SettingsData(TempadLocations.getProviders())

    override fun isAppAvailable(player: Player, stack: ItemStack): Boolean = true
}

data class SettingsData(val providers: Set<ProviderSettings>): MenuContent<SettingsData> {
    companion object {
        val CODEC = ObjectByteCodec.create(
            ProviderSettings.BYTE_CODEC.setOf().fieldOf { it.providers },
            ::SettingsData
        )

        val SERIALIZER = RecordCodecMenuContentSerializer(CODEC)
    }

    override fun serializer(): MenuContentSerializer<SettingsData> = SERIALIZER
}