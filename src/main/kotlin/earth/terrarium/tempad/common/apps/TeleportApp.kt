package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.common.menu.TeleportMenu
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class TeleportApp(val slotId: Int): TempadApp<TeleportData> {
    override fun createMenu(pContainerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return TeleportMenu(pContainerId, inventory, Optional.of(TeleportData(TempadLocations.getAllForPlayer(player), slotId)))
    }

    override fun getDisplayName(): Component = Component.translatable("menu.tempad.teleport")

    override fun createContent(player: ServerPlayer): TeleportData = TeleportData(TempadLocations.getAllForPlayer(player), slotId)

    override fun isEnabled(player: Player): Boolean = true
}

class TeleportData(val locations: Map<ProviderSettings, Map<UUID, LocationData>>, slotId: Int): AppContent<TeleportData>(slotId, CODEC) {
    companion object {
        val CODEC: ByteCodec<TeleportData> = ObjectByteCodec.create(
            ByteCodec.mapOf(
                ProviderSettings.BYTE_CODEC,
                ByteCodec.mapOf(
                    ByteCodec.UUID,
                    LocationData.BYTE_CODEC
                )
            ).fieldOf { it.locations },
            ByteCodec.INT.fieldOf { it.slotId },
            ::TeleportData
        )
    }
}
