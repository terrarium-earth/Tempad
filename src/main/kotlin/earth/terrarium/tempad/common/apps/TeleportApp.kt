package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.common.menu.TeleportMenu
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import java.util.*

object TeleportApp: TempadApp<AvailableTeleportData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return TeleportMenu(pContainerId, pPlayerInventory, Optional.of(AvailableTeleportData(TempadLocations.get(pPlayer).getAll())))
    }

    override fun getDisplayName(): Component = Component.translatable("menu.tempad.teleport")

    override fun createContent(player: ServerPlayer): AvailableTeleportData = AvailableTeleportData(TempadLocations.get(player).getAll())

    override fun isAppAvailable(player: Player, stack: ItemStack): Boolean = true
}

data class AvailableTeleportData(val locations: Map<ProviderSettings, Map<UUID, LocationData>>): MenuContent<AvailableTeleportData> {
    companion object {
        val CODEC = ObjectByteCodec.create(
            ByteCodec.mapOf(
                ProviderSettings.BYTE_CODEC,
                ByteCodec.mapOf(
                    ByteCodec.UUID,
                    LocationData.BYTE_CODEC
                )
            ).fieldOf { it.locations },
            ::AvailableTeleportData
        )

        val SERIALIZER = RecordCodecMenuContentSerializer(CODEC)
    }

    override fun serializer(): MenuContentSerializer<AvailableTeleportData> = SERIALIZER
}
