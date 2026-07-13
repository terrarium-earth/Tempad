package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.data.HistoricalLocation
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.registries.twisterEquipped
import earth.terrarium.tempad.common.utils.DATE_BYTE_CODEC
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

class TimelineApp(val ctx: ItemAccessAddress<*>, val isStationary: Boolean): TempadApp<TimelineData> {
    override fun isEnabled(player: Player): Boolean {
        return ctx.getAccess(player).resource.twisterEquipped
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.TimelineMenu(pContainerId, pPlayerInventory, Optional.of(createContent(pPlayer as ServerPlayer)))
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.timeline")

    override fun createContent(player: ServerPlayer): TimelineData = TimelineData(player.travelHistory, isStationary, ctx)
}

class TimelineData(val history: Map<Date, HistoricalLocation>, isStationary: Boolean, ctx: ItemAccessAddress<*>): AppContent<TimelineData>(ctx, isStationary, codec) {
    companion object {
        val codec = ObjectByteCodec.create(
            ByteCodec.mapOf(DATE_BYTE_CODEC, HistoricalLocation.BYTE_CODEC).fieldOf { it.history },
            ByteCodec.BOOLEAN.fieldOf { it.isStationary },
            ItemAccessAddress.codec.fieldOf { it.ctx },
            ::TimelineData
        )
    }
}
