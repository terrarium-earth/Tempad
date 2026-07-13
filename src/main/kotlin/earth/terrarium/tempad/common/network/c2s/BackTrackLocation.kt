package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.utils.DATE_BYTE_CODEC
import earth.terrarium.tempad.common.utils.stack
import earth.terrarium.tempad.common.utils.testExtract
import net.neoforged.neoforge.transfer.transaction.Transaction
import java.util.*

class BackTrackLocation(val time: Date, val ctx: ItemAccessAddress<*>): Packet<BackTrackLocation> {
    companion object {
        val type = CodecPacketType.Server.create(
            "back_track_location".tempadId,
            ObjectByteCodec.create(
                DATE_BYTE_CODEC.fieldOf { it.time },
                ItemAccessAddress.codec.fieldOf { it.ctx },
                ::BackTrackLocation
            ),
            NetworkHandle.handle { packet, player ->
                val ctx = packet.ctx.getAccess(player)
                val cost = CommonConfig.TimeTwister.costToBacktrack
                if (!player.isCreative && ctx.chronons?.testExtract(cost) ?: false) return@handle
                Transaction.openRoot().use {
                    val extracted = ctx.chronons?.extract(cost, it) ?: 0
                    if (extracted != cost) {
                        return@handle
                    }
                    it.commit()
                }
                player.cooldowns.addCooldown(ctx.stack, 40)
                player.travelHistory.backtrackTo(player, packet.time)
            }
        )
    }

    override fun type(): PacketType<BackTrackLocation> = type
}