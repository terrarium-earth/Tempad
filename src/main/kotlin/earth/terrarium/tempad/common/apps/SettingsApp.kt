package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class SettingsApp(val slotId: Int): TempadApp<SettingsData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.SettingsMenu(
            pContainerId,
            pPlayerInventory,
            Optional.of(SettingsData(TempadLocations.providers, slotId))
        )
    }

    override fun getDisplayName(): Component = Component.translatable("menu.tempad.settings")

    override fun createContent(player: ServerPlayer): SettingsData = SettingsData(TempadLocations.providers, slotId)

    override fun isEnabled(player: Player): Boolean = true
}

class SettingsData(val providers: Set<ProviderSettings>, slotId: Int): AppContent<SettingsData>(slotId, CODEC) {
    companion object {
        val CODEC: ByteCodec<SettingsData> = ObjectByteCodec.create(
            ProviderSettings.BYTE_CODEC.setOf().fieldOf { it.providers },
            ByteCodec.INT.fieldOf { it.slotId },
            ::SettingsData
        )
    }
}