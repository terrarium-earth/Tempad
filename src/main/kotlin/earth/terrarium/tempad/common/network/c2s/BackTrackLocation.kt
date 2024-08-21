package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.drain
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.utils.DATE_BYTE_CODEC
import java.util.*

class BackTrackLocation(val time: Date, val ctx: ContextHolder<*>): Packet<BackTrackLocation> {
    companion object {
        val type = CodecPacketType.Server.create(
            "back_track_location".tempadId,
            ObjectByteCodec.create(
                DATE_BYTE_CODEC.fieldOf { it.time },
                ContextHolder.codec.fieldOf { it.ctx },
                ::BackTrackLocation
            ),
            NetworkHandle.handle { packet, player ->
                val ctx = packet.ctx.getCtx(player)
                if (!player.isCreative && !ctx.drain(1000)) return@handle
                player.cooldowns.addCooldown(ctx.stack.item, 60)
                player.travelHistory.backtrackTo(player, packet.time)
            }
        )
    }

    override fun type(): PacketType<BackTrackLocation> = type
}