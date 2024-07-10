package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.data.HistoricalLocation
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.utils.DATE_BYTE_CODEC
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

class TimelineData(val slot: Int, val history: Map<Date, HistoricalLocation>): AppContent<TimelineData>(slot, CODEC) {
    companion object {
        val CODEC = ObjectByteCodec.create(
            ByteCodec.INT.fieldOf { it.slot },
            ByteCodec.mapOf(DATE_BYTE_CODEC, HistoricalLocation.BYTE_CODEC).fieldOf { it.history },
            ::TimelineData
        )
    }
}

class TimelineApp(val slot: Int): TempadApp<TimelineData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.TimelineMenu(pContainerId, pPlayerInventory, Optional.of(TimelineData(slot, pPlayer.travelHistory)))
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.timeline")

    override fun createContent(player: ServerPlayer): TimelineData = TimelineData(slot, player.travelHistory)
}