package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.PlayerContainerContext
import earth.terrarium.tempad.common.data.pinnedLocation
import earth.terrarium.tempad.common.entity.TimedoorEntity

data class OpenFavoritePacket(val slotId: Int): Packet<OpenFavoritePacket> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "open_favorite".tempadId,
            ByteCodec.INT.map(
                { OpenFavoritePacket(it) },
                { it.slotId },
            ),
            NetworkHandle.handle { message, player ->
                val ctx = PlayerContainerContext(player, message.slotId)
                ctx.pinnedLocation?.let { TimedoorEntity.openTimedoor(ctx, it) }
            }
        )
    }

    override fun type(): PacketType<OpenFavoritePacket> = TYPE
}
